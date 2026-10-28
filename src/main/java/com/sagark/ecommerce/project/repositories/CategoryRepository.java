package com.sagark.ecommerce.project.repositories;

import com.sagark.ecommerce.project.model.Category;

import com.sagark.ecommerce.project.model.CategoryStatus;
import com.sagark.ecommerce.project.paylod.CategoryDistributionDTO;
import com.sagark.ecommerce.project.paylod.TopCategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CategoryRepository extends JpaRepository<Category,Long> {

    Category findByCategoryName(String categoryName);

    long countByCategoryStatus(CategoryStatus categoryStatus);

    @Query("""
    SELECT c.categoryName FROM Categories c LEFT JOIN c.products p GROUP BY c.categoryId, c.categoryName ORDER BY COUNT(p) DESC """)
    List<String> findTopCategoryName(Pageable pageable);

    @Query("""
    SELECT COUNT(p) FROM Categories c LEFT JOIN c.products p WHERE c.categoryName = :categoryName GROUP BY c.categoryId,c.categoryName
""")
    long findProductsByCategoryName(
            @Param("categoryName") String categoryName
    );


    @Query("""
    SELECT c FROM Categories c
    WHERE LOWER(c.categoryName) LIKE :pattern
       OR LOWER(c.description) LIKE :pattern
    """)
    Page<Category> search(@Param("pattern") String pattern, Pageable pageable);

    @Query("""
    SELECT c FROM Categories c
    WHERE c.categoryStatus = :status
      AND (LOWER(c.categoryName) LIKE :pattern
       OR LOWER(c.description) LIKE :pattern)
    """)
    Page<Category> searchByStatus(@Param("pattern") String pattern,
                                  @Param("status") CategoryStatus status,
                                  Pageable pageable);


    @Query("""
select new com.sagark.ecommerce.project.paylod.CategoryDistributionDTO(c.categoryId, c.categoryName, count(p))
from Categories c 
left join c.products p
group by c.categoryId, c.categoryName
ORDER BY COUNT(p) DESC
""")
    List<CategoryDistributionDTO> findCategoryDistribution();
}
