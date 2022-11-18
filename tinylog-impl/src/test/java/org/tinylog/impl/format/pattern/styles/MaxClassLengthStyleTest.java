package org.tinylog.impl.format.pattern.styles;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.tinylog.impl.LogEntry;
import org.tinylog.impl.format.pattern.placeholders.ClassPlaceholder;
import org.tinylog.impl.test.FormatOutputRenderer;
import org.tinylog.impl.test.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class MaxClassLengthStyleTest {

    /**
     * Verifies that the max length style is applied to fully-qualified class names as expected.
     *
     * @param className The fully-qualified class name to which the max length style should be applied
     * @param maxLength The maximum length for the output of the passed class name
     * @param expected The expected result after applying the max length style
     */
    @ParameterizedTest
    @CsvSource({
        /* Fully-qualified class name with legal package */
        "'org.tinylog.impl.style.MyClass', 30, 'org.tinylog.impl.style.MyClass'",
        "'org.tinylog.impl.style.MyClass', 29, 'o.tinylog.impl.style.MyClass'  ",
        "'org.tinylog.impl.style.MyClass', 28, 'o.tinylog.impl.style.MyClass'  ",
        "'org.tinylog.impl.style.MyClass', 27, 'o.t.impl.style.MyClass'        ",
        "'org.tinylog.impl.style.MyClass', 22, 'o.t.impl.style.MyClass'        ",
        "'org.tinylog.impl.style.MyClass', 21, 'o.t.i.style.MyClass'           ",
        "'org.tinylog.impl.style.MyClass', 19, 'o.t.i.style.MyClass'           ",
        "'org.tinylog.impl.style.MyClass', 17, 'o.t.i.s.MyClass'               ",
        "'org.tinylog.impl.style.MyClass', 15, 'o.t.i.s.MyClass'               ",
        "'org.tinylog.impl.style.MyClass', 14, '....s.MyClass'                 ",
        "'org.tinylog.impl.style.MyClass', 13, '....s.MyClass'                 ",
        "'org.tinylog.impl.style.MyClass', 12, '....MyClass'                   ",
        "'org.tinylog.impl.style.MyClass', 11, '....MyClass'                   ",
        "'org.tinylog.impl.style.MyClass', 10, 'MyClass'                       ",
        "'org.tinylog.impl.style.MyClass',  7, 'MyClass'                       ",
        "'org.tinylog.impl.style.MyClass',  6, 'MyC...'                        ",
        "'org.tinylog.impl.style.MyClass',  5, 'My...'                         ",
        "'org.tinylog.impl.style.MyClass',  4, 'M...'                          ",
        "'org.tinylog.impl.style.MyClass',  3, '...'                           ",
        "'org.tinylog.impl.style.MyClass',  2, 'My'                            ",
        "'org.tinylog.impl.style.MyClass',  1, 'M'                             ",
        "'org.tinylog.impl.style.MyClass',  0, ''                              ",
        /* Simple class name without package */
        "'MyClass'                       ,  8, 'MyClass'                       ",
        "'MyClass'                       ,  7, 'MyClass'                       ",
        "'MyClass'                       ,  6, 'MyC...'                        ",
        "'MyClass'                       ,  5, 'My...'                         ",
        "'MyClass'                       ,  4, 'M...'                          ",
        "'MyClass'                       ,  3, '...'                           ",
        "'MyClass'                       ,  2, 'My'                            ",
        "'MyClass'                       ,  1, 'M'                             ",
        "'MyClass'                       ,  0, ''                              ",
        /* Fully-qualified class name with illegal package with leading dot */
        "'.com.foo.MyClass'              , 16, '.com.foo.MyClass'              ",
        "'.com.foo.MyClass'              , 15, '.c.foo.MyClass'                ",
        "'.com.foo.MyClass'              , 14, '.c.foo.MyClass'                ",
        "'.com.foo.MyClass'              , 13, '.c.f.MyClass'                  ",
        "'.com.foo.MyClass'              , 12, '.c.f.MyClass'                  ",
        "'.com.foo.MyClass'              , 11, '....MyClass'                   ",
        "'.com.foo.MyClass'              , 10, 'MyClass'                       ",
        "'.com.foo.MyClass'              ,  7, 'MyClass'                       ",
        "'.com.foo.MyClass'              ,  6, 'MyC...'                        ",
        "'.com.foo.MyClass'              ,  5, 'My...'                         ",
        "'.com.foo.MyClass'              ,  4, 'M...'                          ",
        "'.com.foo.MyClass'              ,  3, '...'                           ",
        "'.com.foo.MyClass'              ,  2, 'My'                            ",
        "'.com.foo.MyClass'              ,  1, 'M'                             ",
        "'.com.foo.MyClass'              ,  0, ''                              ",
        /* Fully-qualified class name with illegal package, containing a double dot */
        "'com..foo.MyClass'              , 16, 'com..foo.MyClass'              ",
        "'com..foo.MyClass'              , 15, 'c..foo.MyClass'                ",
        "'com..foo.MyClass'              , 14, 'c..foo.MyClass'                ",
        "'com..foo.MyClass'              , 13, 'c..f.MyClass'                  ",
        "'com..foo.MyClass'              , 12, 'c..f.MyClass'                  ",
        "'com..foo.MyClass'              , 11, '....MyClass'                   ",
        "'com..foo.MyClass'              , 10, 'MyClass'                       ",
        "'com..foo.MyClass'              ,  7, 'MyClass'                       ",
        "'com..foo.MyClass'              ,  6, 'MyC...'                        ",
        "'com..foo.MyClass'              ,  5, 'My...'                         ",
        "'com..foo.MyClass'              ,  4, 'M...'                          ",
        "'com..foo.MyClass'              ,  3, '...'                           ",
        "'com..foo.MyClass'              ,  2, 'My'                            ",
        "'com..foo.MyClass'              ,  1, 'M'                             ",
        "'com..foo.MyClass'              ,  0, ''                              "
    })
    void apply(String className, int maxLength, String expected) {
        MaxClassLengthStyle style = new MaxClassLengthStyle(new ClassPlaceholder(), maxLength);
        FormatOutputRenderer renderer = new FormatOutputRenderer(style);
        LogEntry logEntry = new LogEntryBuilder().className(className).create();
        assertThat(renderer.render(logEntry)).isEqualTo(expected);
    }

}
