
typedef void OnSuccess();
typedef void OnError(int error);

class BaseListener{
  OnSuccess onSuccess;
  OnError onError;

  BaseListener(this.onSuccess,this.onError);
}