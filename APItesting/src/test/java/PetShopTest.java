
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import endPoints.EndPoints;
import io.restassured.RestAssured;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.Category;
import models.Pet;
import models.Tag;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import utils.Log;
import utilsAPI.APISpecifications;
import utilsAPI.PetStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.put;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class PetShopTest {

    private static RequestSpecification rs = APISpecifications.getRequestSpecification();
    private static File jsonSchema = new File("src/test/resources/petShopJsonSchema.json");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    @DisplayName(" Jsone Scheme validation Test")
    public void testValidateJsonScheme() {
        Log.info("Validate Json scheme test");
        Category category = new Category(1, "Dog");
        Tag tag = new Tag(123, "Huski");

        try {Pet pet = new Pet(9, category, "Jessy", new ArrayList<>(), new ArrayList<Tag>(Collections.singletonList(tag)), PetStatus.AVAILABLE);

        given().spec(rs)
                .when()
                .body(pet)
                .post(EndPoints.PET)
                .then()
                .assertThat()
                .body(matchesJsonSchema(jsonSchema));
        Log.info("JSON Schema is validate!");}
        catch (Exception e) {
           Log.error("JSON Schema isn't validate: " + e.getMessage());
        }


    }


    @Test
    @DisplayName("JsonBody creation")
    public void testPOJO() {
        Log.info("Validate POJO scheme test");

        Tag tag = new Tag(000, "Sphynks");
        Category category = new Category(2, "Cat");
        Pet pet = new Pet(000123465, category, "Cleo", new ArrayList<>(), new ArrayList<>(Collections.singletonList(tag)), PetStatus.PENDING);

        String jsonBody = "";
        try {
            jsonBody = OBJECT_MAPPER.writeValueAsString(pet);
            Log.info("The JsonBody was created successfully!");
        } catch (JsonProcessingException e) {
            Log.error("The error in the JsonBody creation process. Error:" + e.getMessage());
            e.printStackTrace();
        }


        given().spec(rs)
                .when()
                .body(jsonBody)
                .post(EndPoints.PET)
                .then()
                .assertThat()
                .body(matchesJsonSchema(jsonSchema));
    }

    @Test
    @DisplayName("Find pets by status")
    public void findSoldPetsTest() {
        Log.info("Find sold pets test");


        String status = "/pet/findByStatus";
       Response response = given()
                .spec(rs)
                .when()
                .get(status + "?status=sold");

       try {
           Response verify = response.prettyPeek();
           verify.then()
                   .statusCode(200);
           Log.info("The pets with the needed status are found!");
       } catch (Exception e) {
           Log.error("The pets with needed status weren't found! Error: " + e.getMessage());
       }


    }


    @Test
    @DisplayName("Find pet by ID")
    public void findPetByIDTest() {
        Log.info("Find pet by ID");

        String uri = "/pet/";
        Response response = given()
                .spec(rs)
                .when()
                .get(uri + 9);
        try {
            Response verify = response.prettyPeek();
            verify.then()
                    .statusCode(200)
                    .body(matchesJsonSchema(jsonSchema));
            Log.info("The pet was found!");
        } catch (Exception e) {
            Log.error("The pet wasn't found! Error: " + e.getMessage());
        }


    }

    @Test
    @DisplayName("Delete pet by ID")
    public void deletePetByIDTest(){
        Log.info("Delete pet");

        int id = 9;


        String deleteRequest = "https://petstore.swagger.io/v2/pet/";
        RequestSpecification request = RestAssured. given()
                .baseUri(EndPoints.BASEURI)
                .header("Content-Type", "application/json");
        try {
            Response response = request.delete(deleteRequest + id);

            response.prettyPeek()
                    .then()
                    .assertThat()
                    .statusCode(404);
            Log.info("The pet with id " + id + " was deleted successfully!");
        } catch (Exception e) {
            Log.error("The pet wasn't deleted! Error: " + e.getMessage());
        }


    }



@Test
@DisplayName("Create pet")
public Pet createPetTest(){
    Tag tag = new Tag(123, "Good dog");
    Category category = new Category(1, "Dog");
   Pet pet = new Pet(5, category, "Sam", new ArrayList<>(), new ArrayList<>(Collections.singletonList(tag)),PetStatus.PENDING);
        given().spec(rs)
                .when()
                .body(pet)
                .post(EndPoints.PET)
                .then()
                .statusCode(200);


        Log.info("The pet was created successfully!");
    return pet;


}

    @Test
    @DisplayName("Update Pet")
    public void updatePetTest() {
        Log.info("Update pet");
  Pet pet = createPetTest();


        //   int id = 9;
        Tag tag = new Tag(005, "Perside");
        String URI = "https://petstore.swagger.io/v2/pet";
        Category category = new Category(2,"Cat");



        pet.setName("Persik");
        pet.setTags(Collections.singletonList(tag));
        pet.setCategory(category);
        pet.setStatus(PetStatus.AVAILABLE);

      given().spec(rs)
              .when()
              .body(pet)
              .put(EndPoints.PET)
              .then()
              .assertThat()
              .statusCode(200);









    }



}



