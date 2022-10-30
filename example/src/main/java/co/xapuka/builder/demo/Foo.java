package co.xapuka.builder.demo;

import co.xapuka.withbuilder.WithBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


@WithBuilder
public class Foo {

    private static final String CONST = "const";
    private Random rand;
    private int value;
    private String message;
    private List<Long> longs;

    public void foo() {
        //do something
    }

    public String bar() {
        return "";
    }
    public void setRand(Random rand) {
        this.rand = rand;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String[] bar;

    public void setBar(String bar[]) {
        this.bar = bar;
    }

    public void setLongs(List<Long> longs) {
        this.longs = longs;
    }

    @Override
    public String toString() {
        return "Foo{" +
                "rand=" + rand +
                ", value=" + value +
                ", message='" + message + '\'' +
                ", longs=" + longs +
                ", bar=" + Arrays.toString(bar) +
                '}';
    }

    public static void main(String[] args) {

        FooBuilder builder = FooBuilder.newInstance();
        builder.withValue(0);
        Foo foo = FooBuilder.newInstance()
                .withValue(8)
                .withMessage("atp")
                .withBar(null)
                .build();

        System.out.println(foo.toString());



    }


}
