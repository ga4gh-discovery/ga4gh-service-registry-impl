package com.dnastack.dos.registry.repository;

import com.dnastack.dos.registry.model.ServiceNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Data repository of {@link ServiceNode}
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public interface ServiceNodeRepository extends JpaRepository<ServiceNode, String>, JpaSpecificationExecutor {

    /**
     * Fetch a Page of data nodes based on ownerId provided
     *
     * @param ownerId
     * @param pageable
     * @return
     */
    Page<ServiceNode> findByOwnerId(String ownerId, Pageable pageable);

    /**
     * Fetch a Page of data nodes based on ownerId provided
     *
     * @param ownerId
     * @param pageable
     * @return
     */
    Page<ServiceNode> findByOwnerIdAndNameIgnoreCaseContainingAndAliasesIgnoreCaseContainingAndDescriptionIgnoreCaseContaining(String ownerId,
                                                                                                                               String name,
                                                                                                                               String aliases,
                                                                                                                               String description,
                                                                                                                               Pageable pageable);

    Page<ServiceNode> findByOwnerIdAndNameIgnoreCaseContainingAndDescriptionIgnoreCaseContaining(String ownerId,
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
    Page<ServiceNode> findByOwnerIdAndNameLike(String ownerId,
                                               String name,
                                               Pageable pageable);


}
