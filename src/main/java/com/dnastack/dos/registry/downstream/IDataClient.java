package com.dnastack.dos.registry.downstream;

import com.dnastack.dos.registry.downstream.dto.ListDataObjectsResponseDto;
import com.dnastack.dos.registry.model.DataObjectPage;

/**
 * This interface defines api to access downstream data objects node
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public interface IDataClient {

    ListDataObjectsResponseDto getDataObjects();

}
