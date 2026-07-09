package com.elaineparra.productcatalog.product;

import com.elaineparra.productcatalog.product.dto.ProductRequest;
import com.elaineparra.productcatalog.product.dto.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    private ProductService service;

    @BeforeEach
    void setUp() {
        service = new ProductService(repository);
    }

    private Product productWithId(Long id, String name, BigDecimal price, Integer stock) {
        Product product = new Product(name, "a description", price, stock);
        ReflectionTestUtils.setField(product, "id", id);
        return product;
    }

    @Test
    void createSavesAndReturnsProduct() {
        ProductRequest request = new ProductRequest("Widget", "a description", BigDecimal.valueOf(9.99), 10);
        Product saved = productWithId(1L, "Widget", BigDecimal.valueOf(9.99), 10);
        when(repository.save(any(Product.class))).thenReturn(saved);

        ProductResponse response = service.create(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Widget");
        assertThat(response.price()).isEqualByComparingTo("9.99");
        assertThat(response.stock()).isEqualTo(10);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Widget");
    }

    @Test
    void findByIdReturnsProductWhenPresent() {
        Product product = productWithId(1L, "Widget", BigDecimal.valueOf(9.99), 10);
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = service.findById(1L);

        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void findAllDelegatesToRepository() {
        Product product = productWithId(1L, "Widget", BigDecimal.valueOf(9.99), 10);
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);

        Page<ProductResponse> result = service.findAll(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Widget");
    }

    @Test
    void updateModifiesExistingProduct() {
        Product existing = productWithId(1L, "Old name", BigDecimal.valueOf(5.00), 3);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        ProductRequest request = new ProductRequest("New name", "updated description", BigDecimal.valueOf(15.00), 7);

        ProductResponse response = service.update(1L, request);

        assertThat(response.name()).isEqualTo("New name");
        assertThat(response.description()).isEqualTo("updated description");
        assertThat(response.price()).isEqualByComparingTo("15.00");
        assertThat(response.stock()).isEqualTo(7);
    }

    @Test
    void updateThrowsWhenIdDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        ProductRequest request = new ProductRequest("New name", "updated description", BigDecimal.valueOf(15.00), 7);

        assertThatThrownBy(() -> service.update(99L, request))
                .isInstanceOf(ProductNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void deleteRemovesExistingProduct() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteThrowsWhenIdDoesNotExist() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ProductNotFoundException.class);

        verify(repository, never()).deleteById(any());
    }
}
