package utils;

import java.util.ArrayList;
import java.util.HashMap;

import forms.Form;

public record Closure(ArrayList<Form> parameters, ArrayList<Form> body, HashMap<String, Object> environment) {}