package org.openapitools.api;

import org.openapitools.model.Order;
import org.openapitools.model.Pet;
import org.openapitools.repository.OrderRepository;
import org.openapitools.repository.PetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StoreApiDelegateImpl implements StoreApiDelegate {

    private final OrderRepository orderRepository;

    private final PetRepository petRepository;

    public StoreApiDelegateImpl(OrderRepository orderRepository, PetRepository petRepository) {
        this.orderRepository = orderRepository;
        this.petRepository = petRepository;
    }

    @PostConstruct
    void initOrders() {
        orderRepository.save(createOrder(1, 1, Order.StatusEnum.PLACED));
        orderRepository.save(createOrder(2, 1, Order.StatusEnum.DELIVERED));
        orderRepository.save(createOrder(3, 2, Order.StatusEnum.PLACED));
        orderRepository.save(createOrder(4, 2, Order.StatusEnum.DELIVERED));
        orderRepository.save(createOrder(5, 3, Order.StatusEnum.PLACED));
        orderRepository.save(createOrder(6, 3, Order.StatusEnum.PLACED));
        orderRepository.save(createOrder(7, 3, Order.StatusEnum.PLACED));
        orderRepository.save(createOrder(8, 3, Order.StatusEnum.PLACED));
        orderRepository.save(createOrder(9, 3, Order.StatusEnum.PLACED));
        orderRepository.save(createOrder(10, 3, Order.StatusEnum.PLACED));
    }


    @Override
    public ResponseEntity<Void> deleteOrder(String orderId) {
        Order order = orderRepository.findById(Long.valueOf(orderId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        orderRepository.delete(order);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Map<String, Integer>> getInventory() {
        return ResponseEntity.ok(petRepository.findAll().stream()
                .map(Pet::getStatus)
                .collect(Collectors.groupingBy(Pet.StatusEnum::toString, Collectors.reducing(0, e -> 1, Integer::sum))));
    }

    @Override
    public ResponseEntity<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    @Override
    public ResponseEntity<Order> placeOrder(Order order) {
        return ResponseEntity.ok(orderRepository.save(order));
    }

    private static Order createOrder(long id, long petId, Order.StatusEnum status) {
        return new Order()
                .id(id)
                .petId(petId)
                .quantity(2)
                .shipDate(OffsetDateTime.now())
                .status(status);
    }
}
