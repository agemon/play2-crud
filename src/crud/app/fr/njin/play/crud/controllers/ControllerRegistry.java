package fr.njin.play.crud.controllers;

public interface ControllerRegistry {

    <I,T> Crud<I, T> getController(I idClass, T modelClass);
}
