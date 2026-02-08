package com.yatik.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trending")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trending {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // We maintain the foreign key in the 'trending' table.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false, unique = true)
    private Article article;

    // Useful for ordering (e.g., #1 trending vs #10)
    private Integer rank;
}