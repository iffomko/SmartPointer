package org.iffomko.smartPointer;

/**
 * Умный указатель на ресурс (жесткая ссылка). Умный указатель является закрывающиеся структурой данных,
 * следовательно, чтобы освобождать память стоит вызывать метод <code>close()</code>
 * @param <Type> тип ресурса
 */
public class SmartPointer<Type> implements Cloneable, AutoCloseable {
    private volatile DisposerOwner<Type> disposerOwner;

    /**
     * Конструктор, который принимает на вход контроллер этого ресурса
     * @param disposerOwner контроллер ресурса
     * @throws IllegalArgumentException если ресурс уже уничтожен
     */
    SmartPointer(DisposerOwner<Type> disposerOwner) {
        this.disposerOwner = disposerOwner;
        this.disposerOwner.use();
    }

    /**
     * Метод, который закрывает умный указатель и сообщает о том, что этот указатель больше не ссылается на этот ресурс
     * @throws IllegalArgumentException если ресурс уже уничтожен
     */
    @Override
    public void close() {
        DisposerOwner<Type> localOwner = null;

        synchronized (this) {
            localOwner = disposerOwner;

            if (disposerOwner != null) {
                disposerOwner = null;
            }
        }

        if (localOwner != null) {
            localOwner.unUse();
        }
    }

    /**
     * Создает поверхностную копию умного указателя
     * @return копия умного указателя
     */
    @Override
    public SmartPointer<Type> clone() {
        return this.duplicate();
    }

    /**
     * Вспомогательный метод для создания дубликата умного указателя,
     * который тоже ссылается на тот же контроллер ресурсов
     * @return копия умного указателя
     */
    private SmartPointer<Type> duplicate() {
        try {
            SmartPointer<Type> clone = (SmartPointer<Type>) super.clone();

            if (clone.disposerOwner != null) {
                clone.disposerOwner.use();
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException();
        }
    }

    /**
     * Возвращает ресурс, на который ссылается умный указатель
     * @return ресурс
     * @throws IllegalArgumentException если ресурс уже уничтожен
     */
    public Type getTarget() {
        if (disposerOwner != null) {
            return disposerOwner.getTarget();
        }

        throw new IllegalStateException("Попытка получить доступ к разрушенному объекту");
    }
}
