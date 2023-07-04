package ru.practicum.ewm.categories.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.NewCategoryDto;
import ru.practicum.ewm.categories.mapper.CategoryMapper;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        int pageNumber = (int) Math.ceil((double) from / size);
        Pageable pageable = PageRequest.of(pageNumber, size);

        return categoryRepository.findAll(pageable).map(CategoryMapper::toCategoryDto).getContent();
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        return CategoryMapper.toCategoryDto(
                categoryRepository
                        .findById(categoryId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found."))
        );
    }

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is already used.");
        }
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(categoryDto)));
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, NewCategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found."));
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is already used.");
        }
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        categoryRepository.findById(categoryId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found."));
        categoryRepository.deleteById(categoryId);
    }
}
