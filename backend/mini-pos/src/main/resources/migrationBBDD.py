import pandas as pd
from sqlalchemy import create_engine

# -------------------------------
# 1. Conexiones a las dos bases
# -------------------------------

# MySQL (vieja)
mysql_user = "root"
mysql_password = "30285025"
mysql_host = "localhost"
mysql_db = "almacen"

mysql_engine = create_engine(f"mysql+pymysql://{mysql_user}:{mysql_password}@{mysql_host}/{mysql_db}")

# PostgreSQL (nueva)
pg_user = "neondb_owner"
pg_password = "npg_J9qGf3hwgBYe"
pg_host = "ep-round-wave-adx8vbkg-pooler.c-2.us-east-1.aws.neon.tech"
pg_port = "5432"
pg_db = "neondb"

pg_engine = create_engine(f"postgresql+psycopg2://{pg_user}:{pg_password}@{pg_host}:{pg_port}/{pg_db}")

"""
# -------------------------------
# 2. Migrar USUARIOS -> users
# -------------------------------

query_users = "SELECT id, usuario, clave FROM usuarios"
df_users = pd.read_sql(query_users, mysql_engine)

# Adaptar columnas a la nueva estructura
df_users = df_users.rename(columns={
    "idUsuario": "id",
    "usuario": "username",
    "clave": "password"
})

# Campos que no existen en la vieja BD → asignamos valores por defecto
df_users["name"] = df_users["username"]    # puedes cambiarlo si quieres nombre real
df_users["role"] = 0                  # todos como usuarios normales

# Insertar en PostgreSQL
df_users.to_sql("users", pg_engine, if_exists="append", index=False)
print("Usuarios migrados con éxito ✅")

# -------------------------------
# 3. Migrar CLIENTES -> customer
# -------------------------------

query_clients = "SELECT idCliente, nombre, contacto FROM cliente"
df_clients = pd.read_sql(query_clients, mysql_engine)

df_clients["idCliente"] = df_clients["idCliente"].astype(str)

df_clients = df_clients.rename(columns={
    "idCliente": "documento",
    "nombre": "nombre",
    "contacto": "contacto"
})

# Insertar en PostgreSQL
df_clients.to_sql("customer", pg_engine, if_exists="append", index=False)
print("Clientes migrados con éxito ✅") 

# -------------------------------
# 4. Migrar PRODUCTOS -> product
# -------------------------------
query_products = "
                 SELECT idProducto, nombre, precio, precioCompra, marca
                 FROM producto
                 "
df_products = pd.read_sql(query_products, mysql_engine)

# Guardamos idProducto para enlazar luego con inventario
df_products = df_products.rename(columns={
    "idProducto": "id",
    "nombre": "nombre",
    "precio": "precio_venta",
    "precioCompra": "precio_compra",
    "marca": "marca"
})

df_products["creado_en"] = pd.Timestamp.now()
df_products["actualizado_en"] = pd.Timestamp.now()

# Insertar en PostgreSQL
df_products_pg = df_products[["id","nombre", "precio_compra", "precio_venta", "marca", "creado_en", "actualizado_en"]] \
    .copy()
df_products_pg.to_sql("product", pg_engine, if_exists="append", index=False)

print("Productos migrados ✅") 


query_inventory = "
                  SELECT idProducto, cantidadDisponible, ubicacion
                  FROM inventario
                  "
df_inventory = pd.read_sql(query_inventory, mysql_engine)

# Ya tenemos product.id como VARCHAR (el mismo que idProducto)
# No necesitamos buscar por nombre, simplemente usamos idProducto directamente
df_inventory = df_inventory.rename(columns={
    "idProducto": "product_id",
    "cantidadDisponible": "stock",
    "ubicacion": "location"
})

df_inventory["created_at"] = pd.Timestamp.now()
df_inventory["updated_at"] = pd.Timestamp.now()

# Insertar en PostgreSQL
df_inventory_pg = df_inventory[["product_id", "stock", "location", "created_at", "updated_at"]]
df_inventory_pg.to_sql("inventory", pg_engine, if_exists="append", index=False)

print("Inventario migrado ✅")

# -------------------------------
# 6. Migrar TRABAJADORES -> worker
# -------------------------------
query_workers = "SELECT identidad, nombre, puesto FROM trabajador"

df_workers = pd.read_sql(query_workers, mysql_engine)

# Renombrar columnas y adaptar tipos
df_workers = df_workers.rename(columns={
    "identidad": "documento",
    "nombre": "name",
    "puesto": "specialty"
})

# Seleccionar columnas finales
df_workers_pg = df_workers[["documento", "name", "specialty"]]

# Insertar en PostgreSQL
df_workers_pg.to_sql("worker", pg_engine, if_exists="append", index=False)

print("Trabajadores migrados ✅")                  

# -------------------------------
# 7. Migrar FACTURAS -> invoice
# -------------------------------
query_invoices = "SELECT idFactura, cliente, fecha FROM factura "
df_invoices = pd.read_sql(query_invoices, mysql_engine)

# Renombrar columnas
df_invoices = df_invoices.rename(columns={
    "idFactura": "id",
    "cliente": "customer_id",
    "fecha": "sale_date"
})

# Convertir a string
df_invoices["customer_id"] = df_invoices["customer_id"].astype(str)

# Columnas que no existían en la vieja BD
df_invoices["user_id"] = 1   # porque antes no se guardaba trabajador
df_invoices["total"] = 0          # lo calcularemos después con los detalles
df_invoices["payment_type"] = "INMEDIATE"

# Selección final
df_invoices_pg = df_invoices[["id", "sale_date", "customer_id", "user_id" , "total", "payment_type"]]

# Insertar en PostgreSQL
df_invoices_pg.to_sql("sale", pg_engine, if_exists="append", index=False)

print("Facturas migradas ✅") 

# -------------------------------
# 7. Migrar DETALLE_FACTURA -> sale_item
# -------------------------------
query_details = "SELECT idDetalle, idFactura, idProducto, cantidad FROM detalle_factura"
df_details = pd.read_sql(query_details, mysql_engine)

# Traer productos para obtener precio_venta
df_products_pg = pd.read_sql("SELECT id, precio_venta FROM product", pg_engine)

# Merge detalle_factura con productos
df_details = df_details.merge(
    df_products_pg,
    left_on="idProducto",
    right_on="id",
    how="left"
)

# Renombrar y mapear (EXCLUIR "id" del mapeo)
df_details = df_details.rename(columns={
    "idFactura": "sale_id",
    "idProducto": "product_id",
    "cantidad": "cantidad",
    "precio_venta": "precio_unitario"
})

# Asignar descuento default
df_details["descuento"] = 0.0

# Calcular subtotal
df_details["subtotal"] = (
        df_details["cantidad"] * df_details["precio_unitario"] - df_details["descuento"]
)

# Insertar en PostgreSQL (EXCLUIR la columna "id")
df_sale_items_pg = df_details[[
    "cantidad", "descuento", "precio_unitario", "subtotal", "product_id", "sale_id"
]].copy()

df_sale_items_pg.to_sql("sale_item", pg_engine, if_exists="append", index=False)

print("Detalle de facturas migrado ✅")


# -------------------------------
# 8. Actualizar TOTAL en invoices
# -------------------------------
# 1. Leer los sale_items
df_sale_items = pd.read_sql("SELECT sale_id, subtotal FROM sale_item", pg_engine)

# 2. Calcular total por factura
df_totals = df_sale_items.groupby("sale_id")["subtotal"].sum().reset_index()
df_totals = df_totals.rename(columns={"subtotal": "total"})

# 3. Actualizar en invoices
from sqlalchemy import text

with pg_engine.begin() as conn:
    for _, row in df_totals.iterrows():
        conn.execute(
            text("UPDATE sale SET total = :total WHERE id = :sale_id"),
            {"total": float(row["total"]), "sale_id": int(row["sale_id"])}
        )

print("Totales de facturas actualizados ✅") 


# -------------------------------
# 9. Migrar SERVICIOS -> service_order
# -------------------------------
query_services = "SELECT codServicio, descripcion, costo, fecha, idTrabajador FROM servicio"
df_services = pd.read_sql(query_services, mysql_engine)

df_services = df_services.rename(columns={
    "codServicio": "id",
    "descripcion": "description",
    "costo": "labor_cost",
    "fecha": "service_date",
    "idTrabajador": "worker_id"
})

# Columna nueva en la BD actual
df_services["workshop_share"] = 0.3 * df_services["labor_cost"] # o None, si prefieres
df_services["worker_share"] = df_services["labor_cost"] - df_services["workshop_share"]
df_services["total_price"] = df_services["labor_cost"]
df_services["customer_id"] = "0"

# Insertar en PostgreSQL
df_services_pg = df_services[[
    "id", "description", "labor_cost", "service_date", "total_price", "worker_share",
    "workshop_share", "customer_id", "worker_id"
]].copy()

df_services_pg.to_sql("service_order", pg_engine, if_exists="append", index=False)

print("Servicios migrados ✅") """

# -------------------------------
# 8. Migrar DEUDAS -> debt
# -------------------------------
# -------------------------------
# 8. Migrar DEUDAS -> debt
# -------------------------------
query_debt = """SELECT d.id_deuda, d.idCliente, d.monto, d.saldo_restante, d.descripcion,d.fecha_creacion, d.estado, c.nombre AS cliente_nombre FROM deudas d
                      LEFT JOIN cliente c ON d.idCliente = c.idCliente """
df_debt = pd.read_sql(query_debt, mysql_engine)

# Mapear ENUM de estado a boolean (paid)
df_debt["paid"] = df_debt["estado"].apply(lambda x: True if str(x).upper() == "PAGADA" else False)

# Crear campo description incluyendo descripción original + cliente + idCliente
df_debt["description"] = (
        df_debt["descripcion"].fillna("") +
        "  Cliente: " + df_debt["cliente_nombre"].fillna("SIN NOMBRE") +
        " (ID: " + df_debt["idCliente"].astype(str) + ")"
)

# Renombrar columnas
df_debt = df_debt.rename(columns={
    "id_deuda": "id",
    "monto": "total_amount",
    "saldo_restante": "pending_amount",
    "fecha_creacion": "create_at"
})

# Como no hay relación directa con sale_id o service_order_id en la vieja BD, los dejamos NULL
df_debt["sale_id"] = None
df_debt["service_order_id"] = None

# Insertar en PostgreSQL
df_debt_pg = df_debt[["id", "description", "paid", "pending_amount", "total_amount", "create_at", "sale_id", "service_order_id"]]
df_debt_pg.to_sql("debt", pg_engine, if_exists="append", index=False)

print("Deudas migradas ✅")

# -------------------------------
# 9. Migrar PAGOS_DEUDA -> payment
# -------------------------------
query_payment = "SELECT id_pago, id_deuda, monto_pagado, fecha_pago FROM pagos_deuda"
df_payment = pd.read_sql(query_payment, mysql_engine)

# Renombrar columnas
df_payment = df_payment.rename(columns={
    "id_pago": "id",
    "id_deuda": "debt_id",
    "monto_pagado": "amount",
    "fecha_pago": "payment_date"
})

# Insertar en PostgreSQL
df_payment_pg = df_payment[["id", "amount", "payment_date", "debt_id"]]
df_payment_pg.to_sql("payment", pg_engine, if_exists="append", index=False)

print("Pagos de deudas migrados ✅")




