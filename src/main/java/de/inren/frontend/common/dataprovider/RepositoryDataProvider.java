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
package de.inren.frontend.common.dataprovider;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.inren.data.domain.core.DomainObject;

/**
 * Easy to use dataprovider for PagingAndSortingRepository.
 * 
 * The repository must be injected by @SpringBean into the wicket component to
 * avoid serializing problems.
 * 
 * @author Ingo Renner
 *
 */
public class RepositoryDataProvider<T extends DomainObject> extends ARepositoryDataProvider<T> {

    private final PagingAndSortingRepository<T, ? extends Serializable> repository;

    public RepositoryDataProvider(PagingAndSortingRepository<T, ? extends Serializable> repository) {
        super();
        this.repository = repository;
    }

    
    
    
    public RepositoryDataProvider(PagingAndSortingRepository<T, ? extends Serializable> repository, Specification<T> specification) {
        super();
        this.repository = repository;
    }

    @Override
    protected Page<T> getPage(final Pageable pageable) {
            return getRepository().findAll(pageable);
    }

    @Override
    public long size() {
        return getRepository().count();
    }

    public PagingAndSortingRepository<T, ? extends Serializable> getRepository() {
        return repository;
    }
}
