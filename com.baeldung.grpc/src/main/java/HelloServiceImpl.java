import org.baeldung.grpc.HelloResponse;
import org.baeldung.grpc.HelloServiceGrpc;

import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {


    public void hello(org.baeldung.grpc.HelloRequest request,
                      io.grpc.stub.StreamObserver<org.baeldung.grpc.HelloResponse> responseObserver) {

        String greeting = new StringBuilder()
                .append("Hello, ")
                .append(request.getFirstName())
                .append(" ")
                .append(request.getLastName())
                .toString();

        HelloResponse response = HelloResponse.newBuilder()
                .setGreeting(greeting)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }
}
