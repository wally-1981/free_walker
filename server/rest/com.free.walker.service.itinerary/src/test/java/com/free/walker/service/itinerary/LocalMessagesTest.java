package com.free.walker.service.itinerary;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LocalMessagesTest {
    @Test
    public void testGetMessage() {
        assertEquals("This is a message.", LocalMessages.getMessage(LocalMessages.test_message));
    }

    @Test
    public void testGetMessageBy1Arg() {
        assertEquals("This is a message with arg Wuhan.",
            LocalMessages.getMessage(LocalMessages.test_message_with_1_arg, "Wuhan"));
    }

    @Test
    public void testGetMessageBy2Args() {
        assertEquals("This is a message with arg Wuhan and Beijing.",
            LocalMessages.getMessage(LocalMessages.test_message_with_2_args, "Wuhan", "Beijing"));
    }

    @Test
    public void testGetMessageBy3Args() {
        assertEquals("This is a message with arg Wuhan, Beijing and Boston.",
            LocalMessages.getMessage(LocalMessages.test_message_with_3_args, "Wuhan", "Beijing", "Boston"));
    }

    @Test
    public void testGetMessageByMultipleArgs() {
        assertEquals("This is a message with arg Wuhan, Beijing, Boston, Seattle, London.", LocalMessages.getMessage(
            LocalMessages.test_message_with_multiple_args, "Wuhan", "Beijing", "Boston", "Seattle", "London"));
    }
}
