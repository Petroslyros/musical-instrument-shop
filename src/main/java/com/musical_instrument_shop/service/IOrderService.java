package com.musical_instrument_shop.service;

import com.musical_instrument_shop.core.exceptions.AppObjectInvalidArgumentException;
import com.musical_instrument_shop.core.exceptions.AppObjectNotFoundException;
import com.musical_instrument_shop.core.filters.Paginated;
import com.musical_instrument_shop.dto.OrderInsertDTO;
import com.musical_instrument_shop.dto.OrderReadOnlyDTO;
import com.musical_instrument_shop.dto.OrderUpdateDTO;
import org.springframework.data.domain.Pageable;

public interface IOrderService {
    OrderReadOnlyDTO saveOrder(OrderInsertDTO dto) throws AppObjectInvalidArgumentException, AppObjectNotFoundException;
    OrderReadOnlyDTO getOneOrder(Long id) throws AppObjectNotFoundException;
    Paginated<OrderReadOnlyDTO> getPaginatedOrders(Pageable pageable);
    Paginated<OrderReadOnlyDTO> getOrdersByUser(Long userId, Pageable pageable) throws AppObjectNotFoundException;
    OrderReadOnlyDTO updateOrder(OrderUpdateDTO dto) throws AppObjectNotFoundException;
    void deleteOrder(Long id) throws AppObjectNotFoundException;
}