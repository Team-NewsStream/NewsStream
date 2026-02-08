package com.yatik.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "sources")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Source {

    @Id
    @SequenceGenerator(name = "source_seq_gen", sequenceName = "source_seq", allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "source_seq_gen")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "logo_url")
    private String logoUrl;

    @OneToMany(mappedBy = "source", fetch = FetchType.LAZY)
    private List<Article> articles;
}