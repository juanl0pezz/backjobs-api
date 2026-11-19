package com.uap.proiv.jobs.service.impl;

import com.uap.proiv.jobs.client.UserApiRepository;
import com.uap.proiv.jobs.dto.User;
import com.uap.proiv.jobs.dto.UserApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios de UserServiceImpl")
class UserServiceImplTest {

    @Mock
    private UserApiRepository repository;

    private UserServiceImpl service;

    @BeforeEach
    void init() {
        service = new UserServiceImpl(repository);
    }

    @Test
    @DisplayName("search() consulta la pagina indicada y asigna jobId correlativo")
    void search_fetchesPageAndAssignsJobIds() {
        User sampleUser = new User();
        sampleUser.setId(5);
        sampleUser.setFirstName("Ana");

        UserApiResponse mockResponse = new UserApiResponse();
        mockResponse.setPage(2);
        mockResponse.setData(List.of(sampleUser));
        when(repository.getUsers(2)).thenReturn(mockResponse);

        UserApiResponse actual = service.search(2);

        assertThat(actual).isSameAs(mockResponse);
        assertThat(actual.getData().getFirst().getJobId()).isEqualTo(1);
        verify(repository).getUsers(2);
    }

    @Test
    @DisplayName("searchById() obtiene el usuario por ID desde el repositorio")
    void searchById_returnsUserFromRepository() {
        User sampleUser = new User();
        sampleUser.setId(7);
        sampleUser.setFirstName("Ana");
        when(repository.getUserById(7)).thenReturn(sampleUser);

        User actual = service.searchById(7);

        assertThat(actual).isSameAs(sampleUser);
        verify(repository).getUserById(7);
    }
}
