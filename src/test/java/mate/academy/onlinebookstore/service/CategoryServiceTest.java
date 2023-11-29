package mate.academy.onlinebookstore.service;

import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Optional;
import mate.academy.onlinebookstore.dto.category.CategoryDto;
import mate.academy.onlinebookstore.dto.category.CreateCategoryDto;
import mate.academy.onlinebookstore.mapper.BookMapper;
import mate.academy.onlinebookstore.mapper.CategoryMapper;
import mate.academy.onlinebookstore.model.Category;
import mate.academy.onlinebookstore.repository.book.BookRepository;
import mate.academy.onlinebookstore.repository.category.CategoryRepository;
import mate.academy.onlinebookstore.service.category.CategoryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private BookMapper bookMapper;

    @Test
    @DisplayName("Verify save() method works")
    public void save_ValidCreateCategoryDto_ReturnCategoryDto() {
        CreateCategoryDto createCategoryDto = new CreateCategoryDto();
        createCategoryDto.setName("New Test Category");
        createCategoryDto.setDescription("Test description");

        Category category = new Category();
        category.setName(createCategoryDto.getName());
        category.setDescription(createCategoryDto.getDescription());

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(category.getName());
        categoryDto.setDescription(categoryDto.getDescription());

        Mockito.when(categoryMapper.toEntity(createCategoryDto)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto savedCategoryDto = categoryService.save(createCategoryDto);

        Assertions.assertEquals(savedCategoryDto, categoryDto);
        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify getAll method works")
    public void getAll_ValidPageable_ReturnAllCategories() {
        Category category = new Category()
                .setName("Category1")
                .setDescription("Category1");

        CategoryDto categoryDto = new CategoryDto()
                .setName(category.getName())
                .setDescription(category.getDescription());

        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        Mockito.when((categoryMapper.toDto(category))).thenReturn(categoryDto);

        List<CategoryDto> categoryDtos = categoryService.getAll(pageable);

        Assertions.assertEquals(1, categoryDtos.size());
        Assertions.assertEquals(categoryDtos.get(0), categoryDto);
        Mockito.verify(categoryRepository, times(1)).findAll(pageable);
        Mockito.verify(categoryMapper, times(1)).toDto(category);
        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Check if findById() method throws correct "
            + "exception with non existing category id")
    public void findById_GivenNonExistingCategoryId_ShouldThrowException() {
        Long nonExistingCategoryId = 100L;
        Mockito.when(categoryRepository.findById(nonExistingCategoryId))
                .thenReturn(Optional.empty());
        Exception exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> categoryService.findById(nonExistingCategoryId)
        );
        String expected = "Can't find a category by id: " + nonExistingCategoryId;
        String actual = exception.getMessage();
        Assertions.assertEquals(expected, actual);
    }
}
