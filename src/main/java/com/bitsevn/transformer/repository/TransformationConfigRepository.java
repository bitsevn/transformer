package com.bitsevn.transformer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.bitsevn.transformer.model.TransformationConfig;

@Repository
public interface TransformationConfigRepository extends MongoRepository<TransformationConfig, String> {
    
    /**
     * Find configuration by name
     */
    Optional<TransformationConfig> findByName(String name);
    
    /**
     * Find configurations by version
     */
    List<TransformationConfig> findByVersion(String version);
    
    /**
     * Find configurations by description containing text
     */
    List<TransformationConfig> findByDescriptionContainingIgnoreCase(String description);
    
    /**
     * Find configurations by name containing text
     */
    List<TransformationConfig> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find configurations by version and description
     */
    List<TransformationConfig> findByVersionAndDescriptionContainingIgnoreCase(String version, String description);
    
    /**
     * Check if configuration exists by name
     */
    boolean existsByName(String name);
    
    /**
     * Delete configuration by name
     */
    void deleteByName(String name);
    
    /**
     * Count configurations by version
     */
    long countByVersion(String version);
    
    /**
     * Custom query to find configurations with specific XML path patterns in unified mappings
     */
    @Query("{ 'unifiedMappings.xmlPath': { $regex: ?0, $options: 'i' } }")
    List<TransformationConfig> findByXmlPathPattern(String xmlPathPattern);
    
    /**
     * Custom query to find configurations with array type mappings
     */
    @Query("{ 'unifiedMappings.type': 'array' }")
    List<TransformationConfig> findConfigurationsWithArrayMappings();
    
    /**
     * Custom query to find configurations with object type mappings (nested structures)
     */
    @Query("{ 'unifiedMappings.type': 'object' }")
    List<TransformationConfig> findConfigurationsWithObjectMappings();
    
    /**
     * Custom query to find configurations with single type mappings
     */
    @Query("{ 'unifiedMappings.type': 'single' }")
    List<TransformationConfig> findConfigurationsWithSingleMappings();
    
    /**
     * Custom query to find configurations with specific JSON path patterns
     */
    @Query("{ 'unifiedMappings.jsonPath': { $regex: ?0, $options: 'i' } }")
    List<TransformationConfig> findByJsonPathPattern(String jsonPathPattern);
    
    /**
     * Custom query to find configurations with transformations
     */
    @Query("{ 'transformations': { $exists: true, $ne: {} } }")
    List<TransformationConfig> findConfigurationsWithTransformations();
    
    /**
     * Custom query to find configurations with default values
     */
    @Query("{ 'defaultValues': { $exists: true, $ne: {} } }")
    List<TransformationConfig> findConfigurationsWithDefaultValues();
    
    /**
     * Custom query to find configurations by mapping type and depth
     */
    @Query("{ 'unifiedMappings': { $elemMatch: { 'type': ?0, 'children': { $exists: true, $ne: [] } } } }")
    List<TransformationConfig> findByMappingTypeWithChildren(String mappingType);
}
