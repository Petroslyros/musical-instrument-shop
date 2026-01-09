package com.musical_instrument_shop.service;

import com.musical_instrument_shop.core.enums.OrderStatus;
import com.musical_instrument_shop.core.exceptions.AppObjectInvalidArgumentException;
import com.musical_instrument_shop.core.exceptions.AppObjectNotFoundException;
import com.musical_instrument_shop.core.filters.Paginated;
import com.musical_instrument_shop.dto.OrderInsertDTO;
import com.musical_instrument_shop.dto.OrderItemInsertDTO;
import com.musical_instrument_shop.dto.OrderReadOnlyDTO;
import com.musical_instrument_shop.dto.OrderUpdateDTO;
import com.musical_instrument_shop.mapper.Mapper;
import com.musical_instrument_shop.model.Instrument;
import com.musical_instrument_shop.model.Order;
import com.musical_instrument_shop.model.OrderItem;
import com.musical_instrument_shop.model.User;
import com.musical_instrument_shop.repository.InstrumentRepository;
import com.musical_instrument_shop.repository.OrderRepository;
import com.musical_instrument_shop.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService implements IOrderService{

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final InstrumentRepository instrumentRepository;
    private final Mapper mapper;

    @Transactional(rollbackOn = Exception.class)
    public OrderReadOnlyDTO saveOrder(OrderInsertDTO dto) throws AppObjectInvalidArgumentException, AppObjectNotFoundException {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new AppObjectInvalidArgumentException("User", "User with id " + dto.userId() + " not found"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        Set<OrderItem> items = new HashSet<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemInsertDTO itemDTO : dto.items()) {
            Instrument instrument = instrumentRepository.findById(itemDTO.instrumentId())
                    .orElseThrow(() -> new AppObjectInvalidArgumentException("Instrument", "Instrument with id " + itemDTO.instrumentId() + " not found"));

            if (instrument.getStock() < itemDTO.quantity()) {
                throw new AppObjectInvalidArgumentException("Instrument", "Insufficient stock for instrument: " + instrument.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setInstrument(instrument);
            orderItem.setQuantity(itemDTO.quantity());
            orderItem.setPriceAtPurchase(instrument.getPrice());

            items.add(orderItem);
            totalAmount = totalAmount.add(instrument.getPrice().multiply(BigDecimal.valueOf(itemDTO.quantity())));

            // Decrease stock
            instrument.setStock(instrument.getStock() - itemDTO.quantity());
            instrumentRepository.save(instrument);
        }

        order.setItems(items);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        log.info("Order for user={} saved.", dto.userId());
        return mapper.mapToOrderReadOnlyDTO(savedOrder);
    }

    public OrderReadOnlyDTO getOneOrder(Long id) throws AppObjectNotFoundException {
        return orderRepository.findById(id)
                .map(mapper::mapToOrderReadOnlyDTO)
                .orElseThrow(() -> new AppObjectNotFoundException("Order", "Order with id " + id + " not found"));
    }

    public Paginated<OrderReadOnlyDTO> getPaginatedOrders(Pageable pageable) {
        var paginatedOrders = orderRepository.findAll(pageable);
        log.debug("Paginated orders returned successfully");
        return Paginated.fromPage(paginatedOrders.map(mapper::mapToOrderReadOnlyDTO));
    }

    public Paginated<OrderReadOnlyDTO> getOrdersByUser(Long userId, Pageable pageable) throws AppObjectNotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new AppObjectNotFoundException("User", "User with id " + userId + " not found");
        }

        var ordersByUser = orderRepository.findByUser_Id(userId, pageable);
        log.debug("Orders filtered by user={}", userId);
        return Paginated.fromPage(ordersByUser.map(mapper::mapToOrderReadOnlyDTO));
    }

    @Transactional(rollbackOn = Exception.class)
    public OrderReadOnlyDTO updateOrder(OrderUpdateDTO dto) throws AppObjectNotFoundException {
        Order order = orderRepository.findById(dto.id())
                .orElseThrow(() -> new AppObjectNotFoundException("Order", "Order with id " + dto.id() + " not found"));

        order.setStatus(dto.status());
        Order updatedOrder = orderRepository.save(order);
        log.info("Order with id={} updated.", dto.id());
        return mapper.mapToOrderReadOnlyDTO(updatedOrder);
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteOrder(Long id) throws AppObjectNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppObjectNotFoundException("Order", "Order with id " + id + " not found"));
        orderRepository.delete(order);
        log.info("Order with id={} deleted.", id);
    }
}