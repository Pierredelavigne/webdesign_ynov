package com.example.demo_bdd_mongodb_webflux;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public Flux<Item> getAllitems() {
        return itemService.getAllItems();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Item>> getItemById(@PathVariable("id") String id) {
        return itemService.getItemById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<Item> createItem(@RequestBody Item item) {
        return itemService.createItem(item);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id,@RequestBody Item item) {
        return itemService.updateItem(id,item)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteItem(@PathVariable String id) {
        return itemService.deleteItem(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
