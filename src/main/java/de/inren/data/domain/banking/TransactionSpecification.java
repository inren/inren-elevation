/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.inren.data.domain.banking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public class TransactionSpecification implements Specification<Transaction>, Serializable {

    private Transaction filter;
    private Date   from;
    private Date   until;


    public TransactionSpecification(Transaction filter) {
        this.filter = filter;
    }

//    public TransactionSpecification(Transaction filter, Date from, @Nullable Date until) {
//        this.filter = filter;
//        this.from = new Date(from.getTime());
//        this.until = until == null ? null : new Date(until.getTime());
//    }

    
    @Override
    public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        // ok, for each field in filter that is not null we add a predicate
        if (from != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.<Date> get("valutaDate"), from));
        }
        if (until != null) {
            predicates.add(cb.lessThanOrEqualTo(root.<Date> get("valutaDate"), until));
        }
        
        if (filter.getCategory() != null) {
            predicates.add(cb.equal(root.get("category"), filter.getCategory()));
        } else {
            predicates.add(cb.isNull(root.get("category")));
        }
        
        if (filter.getPrincipal() != null) {
            if (filter.getPrincipal().contains("%")) {
                predicates.add(cb.like(root.get("principal"), filter.getPrincipal()));
            } else {
                predicates.add(cb.equal(root.get("principal"), filter.getPrincipal()));
            }
        }
        
        if (filter.getAccountingText() != null) {
            if (filter.getAccountingText().contains("%")) {
                predicates.add(cb.like(root.get("accountingText"), filter.getAccountingText()));
            } else {
                predicates.add(cb.equal(root.get("accountingText"), filter.getAccountingText()));
            }
        }

        if (filter.getPurpose() != null) {
            if (filter.getPurpose().contains("%")) {
                predicates.add(cb.like(root.get("purpose"), filter.getPurpose()));
            } else {
                predicates.add(cb.equal(root.get("purpose"), filter.getPurpose()));
            }
        }
        
        return cb.and(predicates.toArray(new Predicate[] {}));
    }
    
}
