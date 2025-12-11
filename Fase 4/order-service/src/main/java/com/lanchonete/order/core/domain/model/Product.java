package com.lanchonete.order.core.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.lanchonete.order.core.domain.model.enums.ProductCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product {

    @Id
    private String id;
    
    private String name;
    private String description;
    private BigDecimal price;
    private ProductCategoryEnum category;
    
    @Field("created_date")
    private Instant createdDate;
    
    @Field("updated_date")
    private Instant updatedDate;
}



