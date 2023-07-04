package ru.practicum.ewm.categories.service;

import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long categoryId);

    CategoryDto addCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(Long categoryId, NewCategoryDto categoryDto);

    void deleteCategory(Long categoryId);
}
