
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import endPoints.EndPoints;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.Category;
import models.Pet;
import models.Tag;
import org.json.simple.JSONObject;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import utils.Log;
import utilsAPI.APISpecifications;
import utilsAPI.PetStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class PetShopTest {

    private static RequestSpecification rs = APISpecifications.getRequestSpecification();
    private static File jsonSchema = new File("src/test/resources/petShopJsonSchema.json");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void testValidateJsonScheme() {
        Tag tag = new Tag(123, "Huski");
        Category category = new Category(1, "Dog");
        Pet pet = new Pet(9, category, "Jessy", new ArrayList<>(), new ArrayList<Tag>(Collections.singletonList(tag)), PetStatus.AVAILABLE);

        given().spec(rs)
                .when()
                .body(pet)
                .post(EndPoints.PET)
                .then()
                .assertThat()
                .body(matchesJsonSchema(jsonSchema));

    }


    @Test
    public void testPOJO() {
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
    public void findSoldPets() {
        String status = "/pet/findByStatus";
        Response response = given()
                .spec(rs)
                .when()
                .get(status + "?status=sold");

  response.prettyPeek();



    }


    @Test
    public void findPetByID() {
        String uri = "https://petstore.swagger.io/v2/pet/";
        Response response = given()
                .spec(rs)
                .when()
                .get(uri + 9);
        response.prettyPeek();

    }



    @Test
    public void updatePet() {

        int id = 9;
        Tag tag = new Tag(005, "Perside");
        String URI = "https://petstore.swagger.io/v2/pet";


       JSONObject req = new JSONObject();
        req.put("name", "Persik");
        req .put("tags", new ArrayList<>(Collections.singletonList(tag)));
        req.put("status", PetStatus.AVAILABLE);


        Response response = given()
                .when()
                .get(URI);
        response.prettyPeek();





    }

@Test
    public void deletePetByID(){
    int id = 9;

    String deleteRequest = "https://petstore.swagger.io/v2/pet/";
    RequestSpecification request = RestAssured. given();
    request .header("Content-Type", "application/json");
    Response response =  request.delete( deleteRequest + id);
 



}


}



