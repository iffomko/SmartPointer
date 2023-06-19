package org.iffomko.smartPointer;

import java.lang.ref.WeakReference;

/**
 * Слабая ссылка на источник ресурса
 * @param <Type> тип ресурса
 */
public class WeakPointer<Type> {
    private final WeakReference<DisposerOwner<Type>> disposerOwner;

    /**
     * Конструктор, который принимает на вход контроллер ресурса
     * @param disposerOwner контроллер ресурса
     */
    WeakPointer(DisposerOwner<Type> disposerOwner) {
        this.disposerOwner = new WeakReference<>(disposerOwner);
    }

    /**
     * Возвращает ресурс
     * @return ресурс
     * @throws IllegalArgumentException если ресурс уже уничтожен
     */
    public Type getTarget() {
        if (disposerOwner.get() == null) {
            throw new IllegalStateException("Попытка получить доступ к разрушенному объекту");
        }

        return disposerOwner.get().getTarget();
    }
}
