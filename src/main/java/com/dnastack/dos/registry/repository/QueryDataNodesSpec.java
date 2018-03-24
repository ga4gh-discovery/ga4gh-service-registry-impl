package com.dnastack.dos.registry.repository;


import com.dnastack.dos.registry.model.Ga4ghDataNode;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.Map;

/**
 * Query Specification for query data nodes
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
@AllArgsConstructor
public class QueryDataNodesSpec implements Specification<Ga4ghDataNode> {

    private String name;
    private String alias;
    private String description;

    private Map<String, String> meta;

    @Override
    public Predicate toPredicate(Root<Ga4ghDataNode> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        //This part allow to use this specification in pageable queries
        //but you must be aware that the results will be paged in
        //application memory!
        Class clazz = query.getResultType();
        if (clazz.equals(Long.class) || clazz.equals(long.class))
            return null;

        //building the desired query
        MapJoin<Ga4ghDataNode, String, String> metaData = root.joinMap("metaData");
        //root.fetch("aliases", JoinType.LEFT);

        query.distinct(true);
        query.orderBy(cb.asc(root.get("name")));

        //Predicate aliasPredicate = cb.isMember(alias, root.get("aliases"));
        Predicate aliasPredicate = cb.like(root.get("aliases"), formatToLike(alias));
        Predicate namePredicate = cb.like(root.get("name"), formatToLike(name));
        Predicate descPredicate = cb.like(root.get("description"), formatToLike(description));

        if(meta != null && meta.size()>0) {

            Predicate metaPredicate = null;
            for(Map.Entry<String, String> entry : meta.entrySet()) {
                Predicate keyPredicate = cb.equal(metaData.key(), entry.getKey());
                Predicate valuePredicate = cb.equal(metaData.value(), entry.getValue());

                metaPredicate = cb.and(keyPredicate, valuePredicate);
            }

            return cb.and(namePredicate, aliasPredicate, descPredicate, metaPredicate);

        } else {
            return cb.and(namePredicate, aliasPredicate, descPredicate);
        }
    }

    private String formatToLike(String input) {
        return StringUtils.isEmpty(input) ? "%" + input + "%" : "%";
    }

}
