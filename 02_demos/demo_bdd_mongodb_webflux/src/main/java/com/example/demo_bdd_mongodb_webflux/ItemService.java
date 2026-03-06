package com.example.demo_bdd_mongodb_webflux;


import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ItemService {
    private ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }


    public Flux<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Mono<Item> getItemById(String id) {
        return itemRepository.findById(id);
    }


    public Mono<Item> createItem(Item item) {
        return itemRepository.save(item);
    }

    public Mono<Item> updateItem(String id, Item item) {
        return itemRepository.findById(id)
                .flatMap(existingItem -> {
                    existingItem.setName(item.getName());
                    existingItem.setDescription(item.getDescription());
                    existingItem.setPrice(item.getPrice());
                    return itemRepository.save(existingItem);
                });
    }

    public Mono<Void> deleteItem(String id) {
        return itemRepository.deleteById(id);
    }
}
