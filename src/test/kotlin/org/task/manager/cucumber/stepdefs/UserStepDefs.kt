package org.task.manager.cucumber.stepdefs

import io.cucumber.java.Before
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.task.manager.web.rest.UserResource

class UserStepDefs : StepDefs() {

    @Autowired
    private lateinit var userResource: UserResource

    private lateinit var restUserMockMvc: MockMvc

    @Before
    fun setup() {
        restUserMockMvc = MockMvcBuilders.standaloneSetup(userResource).build()
    }

    @When("I search user {string}")
    fun i_search_user(userId: String) {
        actions = restUserMockMvc.perform(get("/api/users/$userId")
                .accept(MediaType.APPLICATION_JSON))
    }

    @Then("the user is found")
    fun the_user_is_found() {
        actions
            ?.andExpect(status().isOk)
            ?.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
    }

    @Then("his last name is {string}")
    fun his_last_name_is(lastName: String) {
        actions?.andExpect(jsonPath("\$.lastName").value(lastName))
    }
}
