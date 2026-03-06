package com.example.demo_backpressure;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/demo")
public class DataProcessingController {
    private final DataProcessingService dataProcessingService;

    public DataProcessingController(DataProcessingService dataProcessingService) {
        this.dataProcessingService = dataProcessingService;
    }

    // --- Endpoints Backpressure ---

    // Flux controle par limitRate : le consommateur tire les elements par lots
    @GetMapping(value = "/backpressure/limit", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Integer> getControlledStream() {
        return dataProcessingService.getControlledStream();
    }

    // Flux avec drop : les elements non consommes a temps sont abandonnes
    @GetMapping(value = "/backpressure/drop", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Integer> getDroppedStream() {
        return dataProcessingService.getDroppedStream();
    }

    // Flux avec buffer : les elements en surplus sont mis en attente
    @GetMapping(value = "/backpressure/buffer", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Integer> getBufferedStream() {
        return dataProcessingService.getBufferedStream();
    }

    // --- Endpoints Schedulers ---

    // publishOn : deplace les operations aval sur un thread dedie
    @GetMapping(value = "/scheduler/publishon", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getPublishOnStream() {
        return dataProcessingService.getPublishOnStream();
    }

    // subscribeOn : deplace toute la source sur un thread dedie
    @GetMapping(value = "/scheduler/subscribeon", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getSubscribeOnStream() {
        return dataProcessingService.getSubscribeOnStream();
    }

    // Traitement parallele avec flatMap + subscribeOn
    @GetMapping(value = "/scheduler/parallel", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getParallelStream() {
        return dataProcessingService.getParallelProcessingStream();
    }
}
