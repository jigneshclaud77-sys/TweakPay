package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.dto.ItemRequestDto;
import com.example.demo.entity.Item;
import com.example.demo.repositories.ItemRepository;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Optional<Item> addItem(ItemRequestDto itemRequestDto){
        Item item= Item.builder().name(itemRequestDto.getName()).amount(itemRequestDto.getAmount()).status(Item.AvailableStatus.valueOf(itemRequestDto.getStatus().toUpperCase())).build();
        return Optional.of(itemRepository.save(item));
    }

    public Optional<Item> getItemById(Long id){
        return itemRepository.findItemById(id);
    }

    public Optional<List<Item>> getAllItems(){
        return Optional.ofNullable(itemRepository.findAll());
    }

}
