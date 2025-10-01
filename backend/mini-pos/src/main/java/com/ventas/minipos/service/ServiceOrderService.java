package com.ventas.minipos.service;

import com.ventas.minipos.domain.*;
import com.ventas.minipos.dto.ProductDTO;
import com.ventas.minipos.dto.ServiceOrderRequest;
import com.ventas.minipos.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServiceOrderService {

    private final ServiceOrderRepository orderRepo;
    private final DebtRepository debtRepository;
    private final CustomerRepository customerRepository;
    private final WorkerRepository workerRepository;
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public ServiceOrder registerService(ServiceOrderRequest request) {

        if (request.getLaborCost() == null || request.getLaborCost() < 0) {
            throw new IllegalArgumentException("La mano de obra es obligatoria y debe ser >= 0");
        }
        if(request.getWorkerId() == null) {
            throw new IllegalArgumentException("El trabajador es obligatorio");
        }

        Customer customer = Optional.ofNullable(request.getCustomerId())
                .flatMap(customerRepository::findById)
                .orElseGet(() -> customerRepository.findByDocumento("0000000000")
                        .orElseThrow(() -> new IllegalStateException("Cliente genérico no configurado en la BD")));

        if (isGenericCustomer(customer) &&
                ("ABONO".equals(request.getPaymentType()) || "DEBT".equals(request.getPaymentType()))) {
            throw new IllegalArgumentException("El cliente genérico no puede tener abonos ni deudas. Solo pagos de contado.");
        }

        ServiceOrder order = new ServiceOrder();
        order.setDescription(request.getDescription());
        order.setLaborCost(request.getLaborCost());
        order.setServiceDate(LocalDateTime.now());

        order.setCustomer(customer);

        Worker worker = workerRepository.findById(request.getWorkerId())
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado"));
        order.setWorker(worker);

        Sale sale = null;
        if (!request.getProducts().isEmpty()) {
            sale = new Sale();
            sale.setCustomer(customer);
            sale.setPaymentType(PaymentType.valueOf(request.getPaymentType()));
            sale.setSaleDate(LocalDateTime.now());
            sale.setItems(new ArrayList<>());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                String username;
                if (principal instanceof UserDetails) {
                    username = ((UserDetails) principal).getUsername();
                } else {
                    username = principal.toString();
                }

                // Buscar usuario en tu repositorio
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado en la base de datos"));
                sale.setUser(user); // 👈 ASIGNAR EL USUARIO
            }
        }

        // AÑADIMOS ESTO PARA OBTENER LA UBICACIÓN DE VENTA
        // Asume que la ubicación viene de una variable o del contexto de la aplicación
        // Si no, podrías pasarlo como un parámetro en la solicitud, ej: request.getSaleLocation()
        // En este ejemplo, lo he añadido como un parámetro del método `registerService`.

        for (ProductDTO item : request.getProducts()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
            }

            // 🔄 VALIDACIÓN Y ACTUALIZACIÓN DEL STOCK EN LA TABLA INVENTORY
            Optional<Inventory> optionalInventory = inventoryRepository.findByProduct(product);
            Inventory inventory = optionalInventory.orElseThrow(() ->
                    new IllegalArgumentException("No se encontró inventario para el producto: " + product.getNombre()));

            if (item.getQuantity() > inventory.getStock()) {
                throw new IllegalArgumentException("Stock insuficiente para: " + product.getNombre() + " en la ubicación: " + inventory.getLocation());
            }

            inventory.setStock(inventory.getStock() - item.getQuantity());
            inventoryRepository.save(inventory);

            order.addproduct(product, item.getQuantity());

            if (sale != null) {
                BigDecimal precioUnitario = product.getPrecioVenta();
                BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(item.getQuantity()));

                SaleItem saleItem = new SaleItem();
                saleItem.setSale(sale);
                saleItem.setProduct(product);
                saleItem.setCantidad(item.getQuantity());
                saleItem.setPrecioUnitario(precioUnitario);
                saleItem.setSubtotal(subtotal);

                sale.getItems().add(saleItem);
            }
        }

        if (sale != null) {
            sale.setTotal(sale.getItems().stream()
                    .map(SaleItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .doubleValue());
            saleRepository.save(sale);
        }

        order.calculateShares();
        ServiceOrder savedOrder = orderRepo.save(order);

        // 📌 Manejo de deuda si es ABONO o DEBT
        if ("ABONO".equals(request.getPaymentType()) || "DEBT".equals(request.getPaymentType())) {

            if (order.getTotalPrice() == null || order.getTotalPrice() <= 0) {
                throw new IllegalStateException("El total del servicio no fue calculado correctamente");
            }

            double abono = request.getAbonoAmount() != null ? request.getAbonoAmount() : 0.0;
            double totalPrice = order.getTotalPrice();
            double pendingAmount = totalPrice - abono;

            Debt debt = Debt.builder()
                    .serviceOrder(savedOrder)
                    .totalAmount(totalPrice)
                    .createAt(LocalDateTime.now())
                    .pendingAmount(pendingAmount)
                    .paid(pendingAmount <= 0)
                    .description("ABONO".equals(request.getPaymentType())
                            ? "Servicio fiado. Abono inicial de " + abono
                            : "Servicio fiado. Sin abono inicial")
                    .build();

            if (abono > 0) {
                Payment payment = Payment.builder()
                        .debt(debt)
                        .amount(abono)
                        .paymentDate(LocalDateTime.now())
                        .build();
                debt.getPayments().add(payment);
            }

            debtRepository.save(debt);
        }

        return savedOrder;
    }

    // 🔎 Helper para identificar cliente genérico
    private boolean isGenericCustomer(Customer customer) {
        return "0".equals(customer.getDocumento());
    }
}

