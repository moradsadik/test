package me.application.java;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) {
        //late/dynamic
        new Circle().draw(new Exporter());
    }

    //early/static
    static void export(Shape s){
        new Exporter().export(s);
    }
}



interface Graphic{ void draw(Exporter e);}
class Shape implements Graphic{ public void draw(Exporter e) { e.export(this);}}
class Circle extends Shape { public void draw(Exporter e) {e.export(this);}}
class Dot extends Shape { public void draw(Exporter e) {e.export(this);}}


class Exporter{
    void export(Shape s){System.out.println("Shape");}
    void export(Dot d){System.out.println("Dot");}
    void export(Circle c){System.out.println("Circle");}
}