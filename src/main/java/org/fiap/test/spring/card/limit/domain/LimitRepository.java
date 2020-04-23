package org.fiap.test.spring.card.limit.domain;

import org.springframework.data.jpa.repository.JpaRepository;

interface LimitRepository extends JpaRepository<Limit, Integer> {
}
