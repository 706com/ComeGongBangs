package com.synergy.backend.domain.orders.model.entity;

import com.synergy.backend.domain.member.model.entity.Member;
import com.synergy.backend.domain.orders.model.type.CartType;
import com.synergy.backend.domain.product.model.entity.Product;
import com.synergy.backend.global.common.BaseResponseStatus;
import com.synergy.backend.global.common.model.BaseEntity;
import com.synergy.backend.global.exception.BaseException;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_idx")
    private Product product;

    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OptionInCart> optionInCarts = new ArrayList<>();

    @Column(nullable = false)
    private Integer count;

    private Integer price;

    private String orderMessage;

    private String optionSummary;

    @Enumerated(EnumType.STRING)
    private CartType cartType;

    public void updateCount(int count) throws BaseException {
        if (count < 1) {
            throw new BaseException(BaseResponseStatus.COUNT_BELOW_ZERO);
        } else if (count > 100) {
            throw new BaseException(BaseResponseStatus.EXCEEDS_MAX_COUNT);
        } else {
            this.count = count;
        }
    }

    public void updatePrice(Integer price) {
        this.price = price;
    }

    public void updateOrderMessage(String orderMessage) {
        this.orderMessage = orderMessage;
    }


}