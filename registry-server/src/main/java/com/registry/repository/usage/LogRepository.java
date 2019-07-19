package com.registry.repository.usage;

import com.registry.repository.image.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Image, Long>{
}
