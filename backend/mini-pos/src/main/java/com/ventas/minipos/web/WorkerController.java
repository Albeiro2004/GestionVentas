package com.ventas.minipos.web;

import com.ventas.minipos.domain.Worker;
import com.ventas.minipos.repo.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/Ventas/workers")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerRepository workerRepo;

    @GetMapping("/all")
    public List<Worker> getAllWorkers() {
        return workerRepo.findAll();
    }
}