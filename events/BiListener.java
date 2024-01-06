package events;

public interface BiListener<A, B> {
	
	void onChange(A a, B b);

}
