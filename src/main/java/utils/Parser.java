package utils;

import java.util.ArrayList;
import java.util.List;

import forms.Form;
import forms.FormList;
import forms.FormLiteral;
import forms.FormSymbol;


//Credits: Claudio @ Anthropic
public class Parser {

    // Marks a token that came from a quoted string literal, so _atom
    // doesn't mistake it for a symbol/number/boolean (matches Python's _String).
    private static class StringToken {
        final String value;
        StringToken(String value) { this.value = value; }
    }

    private static List<Object> tokenize(String text) {
        List<Object> tokens = new ArrayList<>();
        int i = 0, n = text.length();

        while (i < n) {
            char c = text.charAt(i);

            if (c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == ',') {
                i++;
            }
            else if (c == ';') {
                while (i < n && text.charAt(i) != '\n') i++;
            }
            else if (c == '(' || c == ')' || c == '[' || c == ']') {
                tokens.add((c == '(' || c == '[') ? "(" : ")");
                i++;
            }
            else if (c == '"') {
                int j = i + 1;
                StringBuilder buf = new StringBuilder();
                while (j < n && text.charAt(j) != '"') {
                    if (text.charAt(j) == '\\' && j + 1 < n) j++;
                    buf.append(text.charAt(j));
                    j++;
                }
                if (j >= n) throw new RuntimeException("unterminated string literal");
                tokens.add(new StringToken(buf.toString()));
                i = j + 1;
            }
            else {
                int j = i;
                while (j < n && " \t\n\r,()[];\"".indexOf(text.charAt(j)) < 0) j++;
                tokens.add(text.substring(i, j));
                i = j;
            }
        }
        return tokens;
    }

    private static Form atom(Object token) {
        if (token instanceof StringToken st) {
            return new FormLiteral(st.value);
        }
        String tok = (String) token;

        if (tok.equals("true"))  return new FormLiteral(Boolean.TRUE);
        if (tok.equals("false")) return new FormLiteral(Boolean.FALSE);
        if (tok.equals("nil"))   return new FormLiteral(null);

        try {
            return new FormLiteral(Integer.parseInt(tok));
        } catch (NumberFormatException e1) {
            try {
                return new FormLiteral(Float.parseFloat(tok));
            } catch (NumberFormatException e2) {
                return new FormSymbol(tok);
            }
        }
    }

    private static class ReadResult {
        final Form form;
        final int pos;
        ReadResult(Form form, int pos) { this.form = form; this.pos = pos; }
    }

    private static ReadResult read(List<Object> tokens, int pos) {
        if (pos >= tokens.size()) throw new RuntimeException("unexpected end of input");
        Object tok = tokens.get(pos);

        if ("(".equals(tok)) {
            ArrayList<Form> elements = new ArrayList<>();
            pos++;
            while (true) {
                if (pos >= tokens.size()) throw new RuntimeException("missing closing parenthesis");
                if (")".equals(tokens.get(pos))) {
                    return new ReadResult(new FormList(elements), pos + 1);
                }
                ReadResult sub = read(tokens, pos);
                elements.add(sub.form);
                pos = sub.pos;
            }
        }
        if (")".equals(tok)) throw new RuntimeException("unexpected )");

        return new ReadResult(atom(tok), pos + 1);
    }

    public static ArrayList<Form> parse(String text) {
        List<Object> tokens = tokenize(text);
        ArrayList<Form> forms = new ArrayList<>();
        int pos = 0;
        while (pos < tokens.size()) {
            ReadResult result = read(tokens, pos);
            forms.add(result.form);
            pos = result.pos;
        }
        return forms;
    }
}