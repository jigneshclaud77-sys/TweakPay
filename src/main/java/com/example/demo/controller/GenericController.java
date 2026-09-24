package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ItemRequestDto;
import com.example.demo.entity.Item;
import com.example.demo.service.ItemService;

@RestController
public class GenericController {

    @Autowired
    ItemService itemService;

    @PostMapping(path = "/admin/fillItem", consumes = MediaType.APPLICATION_JSON_VALUE)
    // @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addItem(@RequestBody ItemRequestDto item) {
        itemService.addItem(item).orElseThrow();
        return ResponseEntity.ok("Success");
    }

    @GetMapping(path = "/admin/retriveAllItems")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllAvailableUseres() {
        return ResponseEntity.ok(itemService.getAllItems().orElseThrow());
    }

    @GetMapping("/customer/getItemById/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
         return itemService.getItemById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
