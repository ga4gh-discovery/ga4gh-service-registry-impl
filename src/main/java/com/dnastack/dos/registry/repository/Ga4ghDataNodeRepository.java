package com.dnastack.dos.registry.repository;

import com.dnastack.dos.registry.model.Ga4ghDataNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
public interface Ga4ghDataNodeRepository extends JpaRepository<Ga4ghDataNode, String>, JpaSpecificationExecutor {

    /**
     * Fetch a Page of data nodes based on ownerId provided
     *
     * @param ownerId
     * @param pageable
     * @return
     */
    Page<Ga4ghDataNode> findByOwnerId(String ownerId, Pageable pageable);

    /**
     * Fetch a Page of data nodes based on ownerId provided
     *
     * @param ownerId
     * @param pageable
     * @return
     */
    Page<Ga4ghDataNode> findByOwnerIdAndNameIgnoreCaseContainingAndAliasesIgnoreCaseContainingAndDescriptionIgnoreCaseContaining(String ownerId,
                                                                                                                                    String name,
                                                                                        String aliases,
                                                                                        String description,
                                                                                        Pageable pageable);

    Page<Ga4ghDataNode> findByOwnerIdAndNameIgnoreCaseContainingAndDescriptionIgnoreCaseContaining(String ownerId,
                                                                      String name,
                                                                      String description,
                                                                      Pageable pageable);

    /**
     * Fetch a Page of data nodes based on ownerId provided
     *
     * @param ownerId
     * @param pageable
     * @return
     */
    Page<Ga4ghDataNode> findByOwnerIdAndNameLike(String ownerId,
                                                    String name,
                                                    Pageable pageable);


}
