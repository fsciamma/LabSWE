package Cliente;

import java.util.regex.Pattern;

public class Cliente {
    // private static final String EMAIL_REGEX_PATTERN = "^(.+)@(.+).(.+)$";

    private String _nome;
    private String _cognome;

    public Cliente(final String name, final String surname){

        this._nome = name;
        this._cognome = surname;
    }
}
