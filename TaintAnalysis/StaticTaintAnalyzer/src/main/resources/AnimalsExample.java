// javac AnimalsExample.java --release 8 && jar cvf AnimalsExample.jar *.class && rm *.class
public class Activity1 {
    public static void main(String[] args) {
        Animal a;

        // Choose an animal based on user input
        if (args.length == 0) {
            System.out.println("Usage: java AnimalsExample <dog|cat|cow>");
            return;
        }

        String type = args[0].toLowerCase();

        switch (type) {
            case "dog":
                a = new Dog();
                break;
            case "cat":
                a = new Cat();
                break;
            case "cow":
                a = new Cow();
                break;
            default:
                System.out.println("Unknown animal. Defaulting to Dog.");
                a = new Dog();
        }

        a.makeSound(); // Polymorphic call
    }
}

interface Animal {
    void makeSound();
}

class Dog implements Animal {
    public void makeSound() {
        System.out.println("Woof! Woof!");
    }
}

class Cat implements Animal {
    public void makeSound() {
        System.out.println("Meow!");
    }
}

class Cow implements Animal {
    public void makeSound() {
        System.out.println("Moo!");
    }
}
