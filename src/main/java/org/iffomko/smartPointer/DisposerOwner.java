package org.iffomko.smartPointer;

import java.util.function.Consumer;

/**
 * Контроллер закрывающегося ресурса
 * @param <Type> тип ресурса
 */
public class DisposerOwner<Type> {
    private volatile Type target;
    private final IDisposer<? super Type> disposer;
    private volatile int counter;

    /**
     * Конструктора этого контроллера
     * @param target закрывающийся ресурс
     * @param disposer класс, который умеет закрывать ресурс
     */
    DisposerOwner(Type target, IDisposer<? super Type> disposer) {
        this.target = target;
        this.disposer = disposer;
        this.counter = 0;
    }

    /**
     * Проверяет есть ли доступ к ресурсу
     */
    public void checkAccess() {
        if (counter <= 0) {
            throw new IllegalStateException("Попытка получить доступ к разрушенному объекту");
        }
    }

    /**
     * Сообщает контроллеру, что указатель больше не использует ресурс
     * @throws IllegalArgumentException если ресурс уже уничтожен
     */
    public void unUse() {
        Type local = null;

        synchronized (this) {
            checkAccess();

            counter--;

            if (counter == 0) {
                local = target;
                target = null;
            }
        }

        if (local != null) {
            try {
                disposer.dispose(local);
            } catch (Exception e) {
                System.err.println("Ошибка во время разрушения объекта " + e.getMessage());
            }
        }
    }

    /**
     * Сообщает ресурсу, что появился какой-то новый умный указатель на этот ресурс
     * @throws IllegalArgumentException если ресурс уже уничтожен
     */
    public void use() {
        synchronized (this) {
            checkAccess();
            counter++;
        }
    }

    /**
     * Получает жесткую ссылку на ресурс (создает новый умный указатель)
     * @return умный указатель
     * @throws IllegalArgumentException если ресурс уже уничтожен
     */
    public SmartPointer<Type> getPointer() {
        synchronized (this) {
            if (counter > 0) {
                return new SmartPointer<>(this);
            }
        }

        throw new IllegalStateException("Попытка получить доступ к разрушенному объекту");
    }

    /**
     * Создает слабую ссылку умного указателя на ресурс
     * @return слабая ссылка умного указателя
     * @throws IllegalArgumentException если ресурс уже уничтожен
     */
    public WeakPointer<Type> getWeakPointer() {
        synchronized (this) {
            if (counter > 0) {
                return new WeakPointer<>(this);
            }
        }

        throw new IllegalStateException("Попытка использовать разрушенный объект");
    }

    /**
     * Возвращает сам ресурс
     * @return ресурс
     * @throws IllegalArgumentException если ресурс уже уничтожен
     */
    public Type getTarget() {
        checkAccess();
        return target;
    }

    /**
     * Фабричный метод контроллера ресурса, который принимает на вход сам ресурс и его "уничтожитель"
     * @param target ресурс
     * @param disposer уничтожитель ресурса
     * @return контроллер ресурса
     * @param <Type> тип ресурса
     */
    public static <Type> DisposerOwner<Type> newInstance(Type target, IDisposer<Type> disposer) {
        return new DisposerOwner<>(target, disposer);
    }

    /**
     * Фабричный метод контроллера ресурса, который принимает на вход сам ресурс и его функцию, уничтожающая ресурс
     * @param target ресурс
     * @param action функция, уничтожающая ресурс
     * @return контроллер ресурса
     * @param <Type> тип ресурса
     */
    public static <Type> DisposerOwner<Type> newInstance(Type target, Consumer<Type> action) {
        return new DisposerOwner<>(target, new ActionDisposer<Type>(action));
    }

    /**
     * Фабричный метод контроллера ресурса, который принимает на вход сам ресурс
     * @param target ресурс
     * @return контроллер ресурса
     * @param <Type> тип ресурса, который должен наследоваться от <type>Autocloseable</type>
     */
    public static <Type extends AutoCloseable> DisposerOwner<Type> newInstance(Type target) {
        return new DisposerOwner<>(target, DefaultDisposer.INSTANCE);
    }
}
