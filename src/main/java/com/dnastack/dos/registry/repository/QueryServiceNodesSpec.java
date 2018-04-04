package com.dnastack.dos.registry.repository;


import com.dnastack.dos.registry.model.ServiceNodePage;
import com.dnastack.dos.registry.model.ServiceNode;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Query Specification for query data nodes
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
@AllArgsConstructor
public class QueryServiceNodesSpec implements Specification<ServiceNode> {

    private final ServiceNodePage serviceNodePage;

    @Override
    public Predicate toPredicate(Root<ServiceNode> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        //This part allow to use this specification in pageable queries
        //but you must be aware that the results will be paged in
        //application memory!
        Class clazz = query.getResultType();
        if (clazz.equals(Long.class) || clazz.equals(long.class))
            return null;

        //building the desired query
        MapJoin<ServiceNode, String, String> metaData = root.joinMap("metaData");
        //root.fetch("aliases", JoinType.LEFT);

        query.distinct(true);
        query.orderBy(cb.asc(root.get("name")));

        List<Predicate> predicates = new ArrayList<>();
        //Predicate aliasPredicate = cb.isMember(alias, root.get("aliases"));
        predicates.add(cb.like(root.get("aliases"), formatToLike(serviceNodePage.getAlias())));
        predicates.add(cb.like(root.get("name"), formatToLike(serviceNodePage.getName())));
        predicates.add(cb.like(root.get("description"), formatToLike(serviceNodePage.getDescription())));

        if(!CollectionUtils.isEmpty(serviceNodePage.getNodeIds())){
            predicates.add(root.get("id").in(serviceNodePage.getNodeIds()));
        }

        if(!CollectionUtils.isEmpty(serviceNodePage.getMeta())) {

            Predicate metaPredicate = null;
            for(Map.Entry<String, String> entry : serviceNodePage.getMeta().entrySet()) {
                Predicate keyPredicate = cb.equal(metaData.key(), entry.getKey());
                Predicate valuePredicate = cb.equal(metaData.value(), entry.getValue());

                metaPredicate = cb.and(keyPredicate, valuePredicate);
            }

            predicates.add(metaPredicate);

        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private String formatToLike(String input) {
        return !StringUtils.isEmpty(input) ? "%" + input + "%" : "%";
    }

}
