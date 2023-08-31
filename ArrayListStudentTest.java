import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.NoSuchElementException;

/**
 * CS 1332 HW 1
 * ArcWand tests for ArrayList
 *
 * @author Robert Zhu
 * @version 1.0
 */
public class ArrayListStudentTest {
    private static final int TIMEOUT = 50;
    private ArrayList<TestClass> list;

    private static class TestClass {
        private int data;

        public TestClass(int data) {
            this.data = data;
        }
        public TestClass() { this(0); }

        public int getData() {
            return data;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof TestClass)) {
                return false;
            } else {
                TestClass other = (TestClass) o;
                return this.data == other.data;
            }
        }
    }

    private static class Tester extends TestClass {
        public Tester(int data) {
            super(data);
        }
        public Tester() { this(0); }
    }

    // Helper methods to manipulate backingArray
    private int getBackingArrayLength(ArrayList<TestClass> list) {
        return ((Object[]) list.getBackingArray()).length;
    }
    private TestClass getBackingArray(ArrayList<TestClass> list, int index) {
        return (TestClass) ((Object[]) list.getBackingArray())[index];
    }

    @Before
    public void setUp() {
        list = new ArrayList<>();
    }

    @Test(timeout = TIMEOUT)
    public void test_Constructor_BackingArray_Length() {
        TestClass[] expected = new TestClass[ArrayList.INITIAL_CAPACITY];
        assertArrayEquals(expected, list.getBackingArray());
    }

    @Test(timeout = TIMEOUT)
    public void test_Constructor_initial_Size() {
        assertEquals(0, list.size());
    }

    @Test(timeout = TIMEOUT)
    public void test_BackingArray_is_Object_array() { // due to type erasure
        Object[] expected = new Object[]
                { null, null, null, null, null, null, null, null, null};
        assertArrayEquals(expected, list.getBackingArray());
    }

    @Test(timeout = TIMEOUT, expected = IndexOutOfBoundsException.class)
    public void test_addAtIndex_Negative_Index() {
        list.addAtIndex(-1, new TestClass());
    }

    @Test(timeout = TIMEOUT, expected = IndexOutOfBoundsException.class)
    public void test_addAtIndex_Large_Index() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY+5; i++) {
            list.addAtIndex(i, new TestClass(i));
        }

        list.addAtIndex(1000000, new TestClass(6));
    }

    @Test(timeout = TIMEOUT, expected = IndexOutOfBoundsException.class)
    public void test_addAtIndex_Index_Size_Plus_One() {
        list.addAtIndex(0, new TestClass(0));
        list.addAtIndex(1, new TestClass(1));
        list.addAtIndex(2, new TestClass(2));
        list.addAtIndex(3, new TestClass(3));
        list.addAtIndex(4, new TestClass(4));
        list.addAtIndex(5, new TestClass(5));

        list.addAtIndex(7, new TestClass(6));
    }

    @Test(timeout = TIMEOUT, expected = IndexOutOfBoundsException.class)
    public void test_addAtIndex_Index_Size_Plus_One_for_small_array() {
        list.addAtIndex(0, new TestClass(0));

        list.addAtIndex(2, new TestClass(1));
    }

    @Test(timeout = TIMEOUT)
    public void test_addAtIndex_Descriptive_Error_Message_Negative_Index() {
        int index = -1;
        try {
            list.addAtIndex(index, new TestClass());
            assertTrue("Expected IndexOutOfBoundsException", false);
        } catch (IndexOutOfBoundsException e) {
            // Error message contains "negative", "zero", "<", ">", index
            assertTrue(e.getMessage().contains("negative")
                    || e.getMessage().contains("zero")
                    || e.getMessage().contains("<")
                    || e.getMessage().contains(">")
                    || e.getMessage().contains(index + ""));
        }
    }

    @Test(timeout = TIMEOUT)
    public void test_addAtIndex_Descriptive_Error_Message_Large_Index() {
        int index = 1000000;
        try {
            list.addAtIndex(index, new TestClass(0));
            assertTrue("Expected IndexOutOfBoundsException", false);
        } catch (IndexOutOfBoundsException e) {
            // Error message contains "<", ">", index and/or size
            assertTrue(e.getMessage().contains("<")
                    || e.getMessage().contains(">")
                    || e.getMessage().contains(index + "")
                    || e.getMessage().contains(list.size() + ""));
        }
    }

    @Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
    public void test_addAtIndex_Null_Data() {
        list.addAtIndex(0, null);
    }

    @Test(timeout = TIMEOUT, expected = ClassCastException.class)
    public void test_addAtIndex_Binding_Error() {
        ArrayList<Tester> subList = new ArrayList<>();
        subList.addAtIndex(0, (Tester) new TestClass(0));
    }

    @Test(timeout = TIMEOUT)
    public void test_addAtIndex_remains_at_initial_Size_before_Full() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY; i++) {
            list.addAtIndex(i, new TestClass(i));
            assertEquals(ArrayList.INITIAL_CAPACITY, getBackingArrayLength(list));
        }
    }

    @Test(timeout = TIMEOUT)
    public void test_addAtIndex_resize_scales_by_Two_after_Full() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY; i++) {
            list.addAtIndex(i, new TestClass(i));
        }
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY; i++) {
            list.addAtIndex(i, new TestClass(i));
            assertEquals(ArrayList.INITIAL_CAPACITY * 2, getBackingArrayLength(list));
        }
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY; i++) {
            list.addAtIndex(i, new TestClass(i));
            assertEquals(ArrayList.INITIAL_CAPACITY * 4, getBackingArrayLength(list));
        }
    }

    @Test(timeout = TIMEOUT)
    public void test_addAtIndex_allocates_New_array() {
        Object[] oldArray = list.getBackingArray();
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY; i++) {
            list.addAtIndex(i, new TestClass(i));
        }
        list.addAtIndex(ArrayList.INITIAL_CAPACITY, new TestClass(0));
        assertNotSame(oldArray, list.getBackingArray());
    }

    @Test(timeout = TIMEOUT)
    public void test_addAtIndex_Elements_are_Inserted() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY+5; i++) {
            list.addAtIndex(i, new TestClass(i));
        }

        Object[] expected = new Object[]
                { new TestClass(0), new TestClass(1), new TestClass(2), new TestClass(3),
                        new TestClass(4), new TestClass(5), new TestClass(6), new TestClass(7),
                        new TestClass(8), new TestClass(9), new TestClass(10), new TestClass(11),
                        new TestClass(12), new TestClass(13), null, null, null, null };
        assertArrayEquals(expected, list.getBackingArray());
    }

    @Test(timeout = TIMEOUT)
    public void test_addAtIndex_Size_is_updated() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY+5; i++) {
            list.addAtIndex(i, new TestClass(i));
        }
        assertEquals(ArrayList.INITIAL_CAPACITY+5, list.size());
    }

    @Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
    public void test_addToFront_Null_Data() {
        list.addToFront(null);
    }

    @Test(timeout = TIMEOUT, expected = ClassCastException.class)
    public void test_addToFront_Bind_String_to_Integer() {
        ArrayList<Tester> subList = new ArrayList<>();
        subList.addToFront((Tester) new TestClass(0));
    }

    @Test(timeout = TIMEOUT)
    public void test_addToFront_remains_at_initial_Size_before_Full() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY; i++) {
            list.addToFront(new TestClass(i));
            assertEquals(ArrayList.INITIAL_CAPACITY, getBackingArrayLength(list));
        }
    }

    @Test(timeout = TIMEOUT)
    public void test_addToFront_resize_scales_by_Two_after_Full() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY; i++) {
            list.addToFront(new TestClass(i));
        }
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY; i++) {
            list.addToFront(new TestClass(i));
            assertEquals(ArrayList.INITIAL_CAPACITY * 2, getBackingArrayLength(list));
        }
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY; i++) {
            list.addToFront(new TestClass(i));
            assertEquals(ArrayList.INITIAL_CAPACITY * 4, getBackingArrayLength(list));
        }
    }

    @Test(timeout = TIMEOUT)
    public void test_addToFront_allocates_New_array() {
        Object[] oldArray = list.getBackingArray();
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY; i++) {
            list.addToFront(new TestClass(i));
        }
        list.addToFront(new TestClass(0));
        assertNotSame(oldArray, list.getBackingArray());
    }

    @Test(timeout = TIMEOUT)
    public void test_addToFront_Elements_are_Inserted() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY+5; i++) {
            list.addToFront(new TestClass(i));
        }

        Object[] expected = new Object[]
                { new TestClass(13), new TestClass(12), new TestClass(11), new TestClass(10),
                        new TestClass(9), new TestClass(8), new TestClass(7), new TestClass(6),
                        new TestClass(5), new TestClass(4), new TestClass(3), new TestClass(2),
                        new TestClass(1), new TestClass(0), null, null, null, null };
        assertArrayEquals(expected, list.getBackingArray());
    }

    @Test(timeout = TIMEOUT)
    public void test_addToFront_Size_is_updated() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY+5; i++) {
            list.addToFront(new TestClass(i));
        }
        assertEquals(ArrayList.INITIAL_CAPACITY+5, list.size());
    }

    @Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
    public void test_addToBack_Null_Data() {
        list.addToBack(null);
    }

    @Test(timeout = TIMEOUT, expected = ClassCastException.class)
    public void test_addToBack_Bind_String_to_Integer() {
        ArrayList<Tester> subList = new ArrayList<>();
        subList.addToBack((Tester) new TestClass(0));
    }

    @Test(timeout = TIMEOUT)
    public void test_addToBack_remains_at_initial_Size_before_Full() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY; i++) {
            list.addToBack(new TestClass(i));
            assertEquals(ArrayList.INITIAL_CAPACITY, getBackingArrayLength(list));
        }
    }

    @Test(timeout = TIMEOUT)
    public void test_addToBack_resize_scales_by_Two_after_Full() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY; i++) {
            list.addToBack(new TestClass(i));
        }
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY; i++) {
            list.addToBack(new TestClass(i));
            assertEquals(ArrayList.INITIAL_CAPACITY * 2, getBackingArrayLength(list));
        }
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY; i++) {
            list.addToBack(new TestClass(i));
            assertEquals(ArrayList.INITIAL_CAPACITY * 4, getBackingArrayLength(list));
        }
    }

    @Test(timeout = TIMEOUT)
    public void test_addToBack_allocates_New_array() {
        Object[] oldArray = list.getBackingArray();
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY; i++) {
            list.addToBack(new TestClass(i));
        }
        list.addToBack(new TestClass(0));
        assertNotSame(oldArray, list.getBackingArray());
    }

    @Test(timeout = TIMEOUT)
    public void test_addToBack_Elements_are_Inserted() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY+5; i++) {
            list.addToBack(new TestClass(i));
        }

        Object[] expected = new Object[]
                { new TestClass(0), new TestClass(1), new TestClass(2), new TestClass(3),
                        new TestClass(4), new TestClass(5), new TestClass(6), new TestClass(7),
                        new TestClass(8), new TestClass(9), new TestClass(10), new TestClass(11),
                        new TestClass(12), new TestClass(13), null, null, null, null };
        assertArrayEquals(expected, list.getBackingArray());
    }

    @Test(timeout = TIMEOUT)
    public void test_addToBack_Size_is_updated() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY+5; i++) {
            list.addToBack(new TestClass(i));
        }
        assertEquals(ArrayList.INITIAL_CAPACITY+5, list.size());
    }

    @Test(timeout = TIMEOUT, expected = IndexOutOfBoundsException.class)
    public void test_removeAtIndex_Negative_Index() {
        list.removeAtIndex(-1);
    }

    @Test(timeout = TIMEOUT, expected = IndexOutOfBoundsException.class)
    public void test_removeAtIndex_Large_Index() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY+5; i++) {
            list.addAtIndex(i, new TestClass(i));
        }

        list.removeAtIndex(1000000);
    }

    @Test(timeout = TIMEOUT, expected = IndexOutOfBoundsException.class)
    public void test_removeAtIndex_Index_Size_plus_One() {
        list.addAtIndex(0, new TestClass(0));
        list.addAtIndex(1, new TestClass(1));
        list.addAtIndex(2, new TestClass(2));
        list.addAtIndex(3, new TestClass(3));
        list.addAtIndex(4, new TestClass(4));
        list.addAtIndex(5, new TestClass(5));

        list.removeAtIndex(7);
    }

    @Test(timeout = TIMEOUT, expected = IndexOutOfBoundsException.class)
    public void test_removeAtIndex_Index_Size_plus_One_for_Small_array() {
        list.addAtIndex(0, new TestClass(0));

        list.removeAtIndex(2);
    }

    @Test(timeout = TIMEOUT)
    public void test_removeAtIndex_Descriptive_Error_Message_Negative_Index() {
        int index = -1;
        try {
            list.removeAtIndex(index);
            assertTrue("Expected IndexOutOfBoundsException", false);
        } catch (IndexOutOfBoundsException e) {
            // Error message contains "negative", "zero", "<", ">", index
            assertTrue(e.getMessage().contains("negative")
                    || e.getMessage().contains("zero")
                    || e.getMessage().contains("<")
                    || e.getMessage().contains(">")
                    || e.getMessage().contains(index + ""));
        }
    }

    @Test(timeout = TIMEOUT)
    public void test_removeAtIndex_Descriptive_Error_Message_Large_Index() {
        int index = 1000000;
        try {
            list.removeAtIndex(index);
            assertTrue("Expected IndexOutOfBoundsException", false);
        } catch (IndexOutOfBoundsException e) {
            // Error message contains "<", ">", index and/or size
            assertTrue(e.getMessage().contains("<")
                    || e.getMessage().contains(">")
                    || e.getMessage().contains(index + "")
                    || e.getMessage().contains(list.size() + ""));
        }
    }

    @Test(timeout = TIMEOUT)
    public void test_removeAtIndex_does_not_Resize() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY*3; i++) {
            list.addAtIndex(i, new TestClass(i));
        }
        assertEquals(ArrayList.INITIAL_CAPACITY*4, getBackingArrayLength(list));

        for (int i = ArrayList.INITIAL_CAPACITY*3-1; i >= 0; i--) {
            list.removeAtIndex(i);
            assertEquals(ArrayList.INITIAL_CAPACITY*4, getBackingArrayLength(list));
        }
    }

    @Test(timeout = TIMEOUT)
    public void test_removeAtIndex_Shifts_elements() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY+5; i++) {
            list.addAtIndex(i, new TestClass(i));
        }

        Object[] expected = new Object[]
                { new TestClass(0), new TestClass(1), new TestClass(2), new TestClass(3),
                        new TestClass(4), new TestClass(5), new TestClass(6), new TestClass(7),
                        new TestClass(8), new TestClass(9), new TestClass(10), new TestClass(11),
                        new TestClass(12), new TestClass(13), null, null, null, null };
        assertArrayEquals(expected, list.getBackingArray());

        list.removeAtIndex(0);
        expected = new Object[]
                { new TestClass(1), new TestClass(2), new TestClass(3), new TestClass(4),
                        new TestClass(5), new TestClass(6), new TestClass(7), new TestClass(8),
                        new TestClass(9), new TestClass(10), new TestClass(11), new TestClass(12),
                        new TestClass(13), null, null, null, null, null };
        assertArrayEquals(expected, list.getBackingArray());

        list.removeAtIndex(5);
        expected = new Object[]
                { new TestClass(1), new TestClass(2), new TestClass(3), new TestClass(4),
                        new TestClass(5), new TestClass(7), new TestClass(8), new TestClass(9),
                        new TestClass(10), new TestClass(11), new TestClass(12), new TestClass(13),
                        null, null, null, null, null, null };
        assertArrayEquals(expected, list.getBackingArray());

        list.removeAtIndex(10);
        expected = new Object[]
                { new TestClass(1), new TestClass(2), new TestClass(3), new TestClass(4),
                        new TestClass(5), new TestClass(7), new TestClass(8), new TestClass(9),
                        new TestClass(10), new TestClass(11), new TestClass(13), null, null,
                        null, null, null, null, null };
    }

    @Test(timeout = TIMEOUT)
    public void test_removeAtIndex_last_element_is_Null() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY+5; i++) {
            list.addAtIndex(i, new TestClass(i));
        }

        Object[] expected = new Object[]
                { new TestClass(0), new TestClass(1), new TestClass(2), new TestClass(3),
                        new TestClass(4), new TestClass(5), new TestClass(6), new TestClass(7),
                        new TestClass(8), new TestClass(9), new TestClass(10), new TestClass(11),
                        new TestClass(12), new TestClass(13), null, null, null, null };
        assertArrayEquals(expected, list.getBackingArray());

        list.removeAtIndex(1);
        assertEquals(null, getBackingArray(list, 13));
        assertNotEquals(null, getBackingArray(list, 12));

        list.removeAtIndex(2);
        assertEquals(null, getBackingArray(list, 12));
        assertNotEquals(null, getBackingArray(list, 11));

        list.removeAtIndex(3);
        assertEquals(null, getBackingArray(list, 11));
        assertNotEquals(null, getBackingArray(list, 10));

        list.removeAtIndex(4);
        assertEquals(null, getBackingArray(list, 10));
        assertNotEquals(null, getBackingArray(list, 9));

        list.removeAtIndex(5);
        assertEquals(null, getBackingArray(list, 9));
        assertNotEquals(null, getBackingArray(list, 8));
    }

    @Test(timeout = TIMEOUT)
    public void test_removeAtIndex_updates_Size() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY+5; i++) {
            list.addAtIndex(i, new TestClass(i));
        }
        assertEquals(ArrayList.INITIAL_CAPACITY+5, list.size());

        list.removeAtIndex(0);
        assertEquals(ArrayList.INITIAL_CAPACITY+4, list.size());

        list.removeAtIndex(5);
        assertEquals(ArrayList.INITIAL_CAPACITY+3, list.size());

        list.removeAtIndex(10);
        assertEquals(ArrayList.INITIAL_CAPACITY+2, list.size());

        list.removeAtIndex(3);
        assertEquals(ArrayList.INITIAL_CAPACITY+1, list.size());

        list.removeAtIndex(7);
        assertEquals(ArrayList.INITIAL_CAPACITY, list.size());
    }

    @Test(timeout = TIMEOUT)
    public void test_removeAtIndex_Returns_useable_String() {
        ArrayList<String> stringList = new ArrayList<>();
        stringList.addAtIndex(0, "Hello");

        String removed = stringList.removeAtIndex(0);
        assertEquals("Hello", removed);
        assertEquals("H3xxo", removed.replace('e', '3').replace('l', 'x'));
    }

    @Test(timeout = TIMEOUT)
    public void test_removeAtIndex_Returns_Integer_dynamically_binded() {
        ArrayList<Number> numberList = new ArrayList<>();
        numberList.addAtIndex(0, 1);

        Number removed = numberList.removeAtIndex(0);
        assertEquals(1, removed);
        assertEquals(2, removed.intValue() + 1);
        assertTrue(removed instanceof Number);
        assertTrue(removed instanceof Integer);
    }

    @Test(timeout = TIMEOUT, expected = NoSuchElementException.class)
    public void test_removeFromFront_on_Empty_list() {
        list.removeFromFront();
    }

    @Test(timeout = TIMEOUT)
    public void test_removeFromFront_does_not_Resize() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY*3; i++) {
            list.addAtIndex(i, new TestClass(i));
        }
        assertEquals(ArrayList.INITIAL_CAPACITY*4, getBackingArrayLength(list));

        for (int i = ArrayList.INITIAL_CAPACITY*3-1; i >= 0; i--) {
            list.removeFromFront();
            assertEquals(ArrayList.INITIAL_CAPACITY*4, getBackingArrayLength(list));
        }
    }

    @Test(timeout = TIMEOUT)
    public void test_removeFromFront_Shifts_elements() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY+5; i++) {
            list.addAtIndex(i, new TestClass(i));
        }

        Object[] expected = new Object[]
                { new TestClass(0), new TestClass(1), new TestClass(2), new TestClass(3),
                        new TestClass(4), new TestClass(5), new TestClass(6), new TestClass(7),
                        new TestClass(8), new TestClass(9), new TestClass(10), new TestClass(11),
                        new TestClass(12), new TestClass(13), null, null, null, null };
        assertArrayEquals(expected, list.getBackingArray());

        list.removeFromFront();
        expected = new Object[]
                { new TestClass(1), new TestClass(2), new TestClass(3), new TestClass(4),
                        new TestClass(5), new TestClass(6), new TestClass(7), new TestClass(8),
                        new TestClass(9), new TestClass(10), new TestClass(11), new TestClass(12),
                        new TestClass(13), null, null, null, null, null };
        assertArrayEquals(expected, list.getBackingArray());

        list.removeFromFront();
        expected = new Object[]
                { new TestClass(2), new TestClass(3), new TestClass(4), new TestClass(5),
                        new TestClass(6), new TestClass(7), new TestClass(8), new TestClass(9),
                        new TestClass(10), new TestClass(11), new TestClass(12), new TestClass(13),
                        null, null, null, null, null, null };
        assertArrayEquals(expected, list.getBackingArray());

        list.removeFromFront();
        expected = new Object[]
                { new TestClass(3), new TestClass(4), new TestClass(5), new TestClass(6),
                        new TestClass(7), new TestClass(8), new TestClass(9), new TestClass(10),
                        new TestClass(11), new TestClass(12), new TestClass(13), null, null,
                        null, null, null, null, null };
        assertArrayEquals(expected, list.getBackingArray());
    }

    @Test(timeout = TIMEOUT)
    public void test_removeFromFront_last_element_is_Null() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY+5; i++) {
            list.addAtIndex(i, new TestClass(i));
        }

        Object[] expected = new Object[]
                { new TestClass(0), new TestClass(1), new TestClass(2), new TestClass(3),
                        new TestClass(4), new TestClass(5), new TestClass(6), new TestClass(7),
                        new TestClass(8), new TestClass(9), new TestClass(10), new TestClass(11),
                        new TestClass(12), new TestClass(13), null, null, null, null };
        assertArrayEquals(expected, list.getBackingArray());

        list.removeFromFront();
        assertEquals(null, getBackingArray(list, 13));
        assertNotEquals(null, getBackingArray(list, 12));

        list.removeFromFront();
        assertEquals(null, getBackingArray(list, 12));
        assertNotEquals(null, getBackingArray(list, 11));

        list.removeFromFront();
        assertEquals(null, getBackingArray(list, 11));
        assertNotEquals(null, getBackingArray(list, 10));

        list.removeFromFront();
        assertEquals(null, getBackingArray(list, 10));
        assertNotEquals(null, getBackingArray(list, 9));

        list.removeFromFront();
        assertEquals(null, getBackingArray(list, 9));
        assertNotEquals(null, getBackingArray(list, 8));
    }

    @Test(timeout = TIMEOUT)
    public void test_removeFromFront_updates_Size() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY+5; i++) {
            list.addAtIndex(i, new TestClass(i));
        }
        assertEquals(ArrayList.INITIAL_CAPACITY+5, list.size());

        list.removeFromFront();
        assertEquals(ArrayList.INITIAL_CAPACITY+4, list.size());

        list.removeFromFront();
        assertEquals(ArrayList.INITIAL_CAPACITY+3, list.size());

        list.removeFromFront();
        assertEquals(ArrayList.INITIAL_CAPACITY+2, list.size());

        list.removeFromFront();
        assertEquals(ArrayList.INITIAL_CAPACITY+1, list.size());

        list.removeFromFront();
        assertEquals(ArrayList.INITIAL_CAPACITY, list.size());
    }

    @Test(timeout = TIMEOUT)
    public void test_removeFromFront_Returns_useable_String() {
        ArrayList<String> stringList = new ArrayList<>();
        stringList.addAtIndex(0, "Hello");

        String removed = stringList.removeFromFront();
        assertEquals("Hello", removed);
        assertEquals("H3xxo", removed.replace('e', '3').replace('l', 'x'));
    }

    @Test(timeout = TIMEOUT)
    public void test_removeFromFront_Returns_Integer_dynamically_binded() {
        ArrayList<Number> numberList = new ArrayList<>();
        numberList.addAtIndex(0, 1);

        Number removed = numberList.removeFromFront();
        assertEquals(1, removed);
        assertEquals(2, removed.intValue() + 1);
        assertTrue(removed instanceof Number);
        assertTrue(removed instanceof Integer);
    }

    @Test(timeout = TIMEOUT, expected = NoSuchElementException.class)
    public void test_removeFromBack_on_Empty_list() {
        list.removeFromBack();
    }

    @Test(timeout = TIMEOUT)
    public void test_removeFromBack_does_not_Resize() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY*3; i++) {
            list.addAtIndex(i, new TestClass(i));
        }
        assertEquals(ArrayList.INITIAL_CAPACITY*4, getBackingArrayLength(list));

        for (int i = ArrayList.INITIAL_CAPACITY*3-1; i >= 0; i--) {
            list.removeFromBack();
            assertEquals(ArrayList.INITIAL_CAPACITY*4, getBackingArrayLength(list));
        }
    }

    @Test(timeout = TIMEOUT)
    public void test_removeFromBack_Shifts_elements() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY+5; i++) {
            list.addAtIndex(i, new TestClass(i));
        }

        Object[] expected = new Object[]
                { new TestClass(0), new TestClass(1), new TestClass(2), new TestClass(3),
                        new TestClass(4), new TestClass(5), new TestClass(6), new TestClass(7),
                        new TestClass(8), new TestClass(9), new TestClass(10), new TestClass(11),
                        new TestClass(12), new TestClass(13), null, null, null, null };
        assertArrayEquals(expected, list.getBackingArray());

        list.removeFromBack();
        expected = new Object[]
                { new TestClass(0), new TestClass(1), new TestClass(2), new TestClass(3),
                        new TestClass(4), new TestClass(5), new TestClass(6), new TestClass(7),
                        new TestClass(8), new TestClass(9), new TestClass(10), new TestClass(11),
                        new TestClass(12), null, null, null, null, null };
        assertArrayEquals(expected, list.getBackingArray());

        list.removeFromBack();
        expected = new Object[]
                { new TestClass(0), new TestClass(1), new TestClass(2), new TestClass(3),
                        new TestClass(4), new TestClass(5), new TestClass(6), new TestClass(7),
                        new TestClass(8), new TestClass(9), new TestClass(10), new TestClass(11),
                        null, null, null, null, null, null };
        assertArrayEquals(expected, list.getBackingArray());

        list.removeFromBack();
        expected = new Object[]
                { new TestClass(0), new TestClass(1), new TestClass(2), new TestClass(3),
                        new TestClass(4), new TestClass(5), new TestClass(6), new TestClass(7),
                        new TestClass(8), new TestClass(9), new TestClass(10), null, null,
                        null, null, null, null, null };
        assertArrayEquals(expected, list.getBackingArray());
    }

    @Test(timeout = TIMEOUT)
    public void test_removeFromBack_last_element_is_Null() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY+5; i++) {
            list.addAtIndex(i, new TestClass(i));
        }

        Object[] expected = new Object[]
                { new TestClass(0), new TestClass(1), new TestClass(2), new TestClass(3),
                        new TestClass(4), new TestClass(5), new TestClass(6), new TestClass(7),
                        new TestClass(8), new TestClass(9), new TestClass(10), new TestClass(11),
                        new TestClass(12), new TestClass(13), null, null, null, null };
        assertArrayEquals(expected, list.getBackingArray());

        list.removeFromBack();
        assertEquals(null, getBackingArray(list, 13));
        assertNotEquals(null, getBackingArray(list, 12));

        list.removeFromBack();
        assertEquals(null, getBackingArray(list, 12));
        assertNotEquals(null, getBackingArray(list, 11));

        list.removeFromBack();
        assertEquals(null, getBackingArray(list, 11));
        assertNotEquals(null, getBackingArray(list, 10));

        list.removeFromBack();
        assertEquals(null, getBackingArray(list, 10));
        assertNotEquals(null, getBackingArray(list, 9));

        list.removeFromBack();
        assertEquals(null, getBackingArray(list, 9));
        assertNotEquals(null, getBackingArray(list, 8));
    }

    @Test(timeout = TIMEOUT)
    public void test_removeFromBack_updates_Size() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY+5; i++) {
            list.addAtIndex(i, new TestClass(i));
        }
        assertEquals(ArrayList.INITIAL_CAPACITY+5, list.size());

        list.removeFromBack();
        assertEquals(ArrayList.INITIAL_CAPACITY+4, list.size());

        list.removeFromBack();
        assertEquals(ArrayList.INITIAL_CAPACITY+3, list.size());

        list.removeFromBack();
        assertEquals(ArrayList.INITIAL_CAPACITY+2, list.size());

        list.removeFromBack();
        assertEquals(ArrayList.INITIAL_CAPACITY+1, list.size());

        list.removeFromBack();
        assertEquals(ArrayList.INITIAL_CAPACITY, list.size());
    }

    @Test(timeout = TIMEOUT)
    public void test_removeFromBack_Returns_useable_String() {
        ArrayList<String> stringList = new ArrayList<>();
        stringList.addAtIndex(0, "Hello");

        String removed = stringList.removeFromBack();
        assertEquals("Hello", removed);
        assertEquals("H3xxo", removed.replace('e', '3').replace('l', 'x'));
    }

    @Test(timeout = TIMEOUT)
    public void test_removeFromBack_Returns_Integer_dynamically_binded() {
        ArrayList<Number> numberList = new ArrayList<>();
        numberList.addAtIndex(0, 1);

        Number removed = numberList.removeFromBack();
        assertEquals(1, removed);
        assertEquals(2, removed.intValue() + 1);
        assertTrue(removed instanceof Number);
        assertTrue(removed instanceof Integer);
    }

    @Test(timeout = TIMEOUT, expected = IndexOutOfBoundsException.class)
    public void test_get_Negative_Index() {
        list.get(-1);
    }

    @Test(timeout = TIMEOUT, expected = IndexOutOfBoundsException.class)
    public void test_get_Large_Index() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY+5; i++) {
            list.addAtIndex(i, new TestClass(i));
        }

        list.get(1000000);
    }

    @Test(timeout = TIMEOUT, expected = IndexOutOfBoundsException.class)
    public void test_get_Index_Size_plus_One() {
        list.addAtIndex(0, new TestClass(0));
        list.addAtIndex(1, new TestClass(1));
        list.addAtIndex(2, new TestClass(2));
        list.addAtIndex(3, new TestClass(3));
        list.addAtIndex(4, new TestClass(4));
        list.addAtIndex(5, new TestClass(5));

        list.get(7);
    }

    @Test(timeout = TIMEOUT, expected = IndexOutOfBoundsException.class)
    public void test_get_Index_Size_plus_One_for_Small_array() {
        list.addAtIndex(0, new TestClass(0));

        list.get(2);
    }

    @Test(timeout = TIMEOUT)
    public void test_get_Descriptive_Error_Message_Negative_Index() {
        int index = -1;
        try {
            list.addAtIndex(index, new TestClass());
            assertTrue("Expected IndexOutOfBoundsException", false);
        } catch (IndexOutOfBoundsException e) {
            // Error message contains "negative", "zero", "<", ">", index
            assertTrue(e.getMessage().contains("negative")
                    || e.getMessage().contains("zero")
                    || e.getMessage().contains("<")
                    || e.getMessage().contains(">")
                    || e.getMessage().contains(index + ""));
        }
    }

    @Test(timeout = TIMEOUT)
    public void test_get_Descriptive_Error_Message_Large_Index() {
        int index = 1000000;
        try {
            list.addAtIndex(index, new TestClass(0));
            assertTrue("Expected IndexOutOfBoundsException", false);
        } catch (IndexOutOfBoundsException e) {
            // Error message contains "<", ">", index and/or size
            assertTrue(e.getMessage().contains("<")
                    || e.getMessage().contains(">")
                    || e.getMessage().contains(index + "")
                    || e.getMessage().contains(list.size() + ""));
        }
    }

    @Test(timeout = TIMEOUT)
    public void test_get_Returns_useable_String() {
        ArrayList<String> stringList = new ArrayList<>();
        stringList.addAtIndex(0, "Hello");

        String gotten = stringList.get(0);
        assertEquals("Hello", gotten);
        assertEquals("H3xxo", gotten.replace('e', '3').replace('l', 'x'));
    }

    @Test(timeout = TIMEOUT)
    public void test_get_Returns_Integer_dynamically_binded() {
        ArrayList<Number> numberList = new ArrayList<>();
        numberList.addAtIndex(0, 1);

        Number gotten = numberList.get(0);
        assertEquals(1, gotten);
        assertEquals(2, gotten.intValue() + 1);
        assertTrue(gotten instanceof Number);
        assertTrue(gotten instanceof Integer);
    }

    @Test(timeout = TIMEOUT)
    public void test_clear_Allocates_New_array() {
        Object[] oldArray = list.getBackingArray();
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY; i++) {
            list.addAtIndex(i, new TestClass(i));
        }

        list.clear();
        assertNotSame(oldArray, list.getBackingArray());
    }

    @Test(timeout = TIMEOUT)
    public void test_clear_BackingArray_Length() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY; i++) {
            list.addAtIndex(i, new TestClass(i));
        }

        list.clear();
        assertEquals(ArrayList.INITIAL_CAPACITY, getBackingArrayLength(list));
    }

    @Test(timeout = TIMEOUT)
    public void test_clear_sets_Size_to_Zero() {
        for (int i = 0; i < ArrayList.INITIAL_CAPACITY; i++) {
            list.addAtIndex(i, new TestClass(i));
        }

        list.clear();
        assertEquals(0, list.size());
    }
}