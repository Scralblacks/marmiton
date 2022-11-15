package objet;

import java.time.LocalDate;

public class Recipe {
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getListIdIngredient() {
        return listIdIngredient;
    }

    public void setListIdIngredient(String listIdIngredient) {
        this.listIdIngredient = listIdIngredient;
    }

    public String getListIdCookingTool() {
        return listIdCookingTool;
    }

    public void setListIdCookingTool(String listIdCookingTool) {
        this.listIdCookingTool = listIdCookingTool;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getWhenLastCooked() {
        return whenLastCooked;
    }

    public void setWhenLastCooked(LocalDate whenLastCooked) {
        this.whenLastCooked = whenLastCooked;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    private long id;
    private String name, instruction, listIdIngredient, listIdCookingTool, description;
    private LocalDate whenLastCooked;

    public Recipe(long id, String name, String instruction, String listIdIngredient, String listIdCookingTool, LocalDate whenLastCooked, String description){
        this.id = id;
        this.name = name;
        this.instruction = instruction;
        this.listIdIngredient = listIdIngredient;
        this.listIdCookingTool = listIdCookingTool;
        this.whenLastCooked = whenLastCooked;
        this.description = description;
    }
}
