package org.poo.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;

public interface CommandPattern {

    void execute(CommandInput command, ObjectMapper obj, ArrayNode output, Bank bank);
}
