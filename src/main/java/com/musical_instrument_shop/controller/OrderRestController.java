package com.musical_instrument_shop.controller;

import com.musical_instrument_shop.core.enums.OrderStatus;
import com.musical_instrument_shop.core.exceptions.AppObjectInvalidArgumentException;
import com.musical_instrument_shop.core.exceptions.AppObjectNotFoundException;
import com.musical_instrument_shop.core.exceptions.ValidationException;
import com.musical_instrument_shop.core.filters.GenericFilters;
import com.musical_instrument_shop.core.filters.Paginated;
import com.musical_instrument_shop.dto.OrderInsertDTO;
import com.musical_instrument_shop.dto.OrderReadOnlyDTO;
import com.musical_instrument_shop.dto.OrderUpdateDTO;
import com.musical_instrument_shop.dto.ResponseMessageDTO;
import com.musical_instrument_shop.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderRestController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderReadOnlyDTO> saveOrder(
            @Valid @RequestBody OrderInsertDTO dto,
            BindingResult bindingResult)
            throws AppObjectInvalidArgumentException, AppObjectNotFoundException, ValidationException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        OrderReadOnlyDTO orderReadOnlyDTO = orderService.saveOrder(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(orderReadOnlyDTO.id())
                .toUri();

        return ResponseEntity.created(location).body(orderReadOnlyDTO);
    }

    @GetMapping
    public ResponseEntity<Paginated<OrderReadOnlyDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        GenericFilters filters = new GenericFilters() {};
        filters.setPage(page);
        filters.setPageSize(size);
        filters.setSortBy(sortBy);
        filters.setSortDirection(sortDirection.equalsIgnoreCase("DESC") ?
                Sort.Direction.DESC : Sort.Direction.ASC);

        Paginated<OrderReadOnlyDTO> orders = orderService.getPaginatedOrders(filters.getPageable());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderReadOnlyDTO> getOrderById(@PathVariable Long id)
            throws AppObjectNotFoundException {
        OrderReadOnlyDTO order = orderService.getOneOrder(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Paginated<OrderReadOnlyDTO>> getOrdersByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection)
            throws AppObjectNotFoundException {

        GenericFilters filters = new GenericFilters() {};
        filters.setPage(page);
        filters.setPageSize(size);
        filters.setSortBy(sortBy);
        filters.setSortDirection(sortDirection.equalsIgnoreCase("DESC") ?
                Sort.Direction.DESC : Sort.Direction.ASC);

        Paginated<OrderReadOnlyDTO> orders = orderService.getOrdersByUser(userId, filters.getPageable());
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderReadOnlyDTO> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderUpdateDTO dto,
            BindingResult bindingResult)
            throws AppObjectNotFoundException, ValidationException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        OrderReadOnlyDTO orderReadOnlyDTO = orderService.updateOrder(dto);
        return ResponseEntity.ok(orderReadOnlyDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO> deleteOrder(@PathVariable Long id)
            throws AppObjectNotFoundException {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(new ResponseMessageDTO("Success", "Order deleted successfully"));
    }
}