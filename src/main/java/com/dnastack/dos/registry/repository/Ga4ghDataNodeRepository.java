package com.dnastack.dos.registry.repository;

import com.dnastack.dos.registry.model.Ga4ghDataNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Data repository of {@link com.dnastack.dos.registry.model.Ga4ghDataNode}
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public interface Ga4ghDataNodeRepository extends JpaRepository<Ga4ghDataNode, String>{

    /**
     * Fetch a Page of data nodes based on customerId provided
     *
     * @param customerId
     * @param pageable
     * @return
     */
    Page<Ga4ghDataNode> findByCustomerId(String customerId, Pageable pageable);

    /**
     * Fetch a Page of data nodes based on customerId provided
     *
     * @param customerId
     * @param pageable
     * @return
     */
    Page<Ga4ghDataNode> findByCustomerIdAndNameIgnoreCaseContainingAndAliasesIgnoreCaseContainingAndDescriptionIgnoreCaseContaining(String customerId,
                                                                                                                                    String name,
                                                                                        String aliases,
                                                                                        String description,
                                                                                        Pageable pageable);

    Page<Ga4ghDataNode> findByCustomerIdAndNameIgnoreCaseContainingAndDescriptionIgnoreCaseContaining(String customerId,
                                                                      String name,
                                                                      String description,
                                                                      Pageable pageable);

    /**
     * Fetch a Page of data nodes based on customerId provided
     *
     * @param customerId
     * @param pageable
     * @return
     */
    Page<Ga4ghDataNode> findByCustomerIdAndNameLike(String customerId,
                                                    String name,
                                                    Pageable pageable);

}
