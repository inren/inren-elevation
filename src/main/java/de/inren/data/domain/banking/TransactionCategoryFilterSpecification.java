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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * @author Ingo Renner
 *
 */
public class TransactionCategoryFilterSpecification implements Specification<Transaction> {

    private CategoryFilter filter;

    public TransactionCategoryFilterSpecification(CategoryFilter category) {
        this.filter = category;
    }

    @Override
    public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        if (!StringUtils.isEmpty(filter.getPrincipalFilter())) {
            predicates.add(createPredicate(root, cb, adjustWildcards(filter.getPrincipalFilter()), "principal"));
        }
        if (!StringUtils.isEmpty(filter.getAccountingTextFilter())) {
            predicates.add(createPredicate(root, cb, adjustWildcards(filter.getAccountingTextFilter()), "accountingText"));
        }
        if (!StringUtils.isEmpty(filter.getPurposeFilter())) {
            predicates.add(createPredicate(root, cb, adjustWildcards(filter.getPurposeFilter()), "purpose"));
        }
        return cb.and(predicates.toArray(new Predicate[] {}));
    }

    private Predicate createPredicate(Root<Transaction> root, CriteriaBuilder cb, String filterValue, String property) {
        if (filterValue.contains("%")) {
            return (cb.like(root.get(property), filterValue));
        } else {
            return (cb.equal(root.get(property), filterValue));
        }
    }

    // replace * with %
    private String adjustWildcards(String principalFilter) {
        return principalFilter.replaceAll("\\*", "%");
    }
}
