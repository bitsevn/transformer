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
     * Custom query to find configurations with specific property mappings
     */
    @Query("{ 'propertyMappings.xmlPath': { $regex: ?0, $options: 'i' } }")
    List<TransformationConfig> findByXmlPathPattern(String xmlPathPattern);
    
    /**
     * Custom query to find configurations with specific array mappings
     */
    @Query("{ 'arrayMappings': { $exists: true, $ne: {} } }")
    List<TransformationConfig> findConfigurationsWithArrayMappings();
    
    /**
     * Custom query to find configurations with nested property mappings
     */
    @Query("{ 'nestedPropertyMappings': { $exists: true, $ne: [] } }")
    List<TransformationConfig> findConfigurationsWithNestedMappings();
}
