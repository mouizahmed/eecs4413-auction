import { signIn, signOut } from '../requests/postRequests';
import { getCurrentUser } from '../requests/getRequests';

export const doSignIn = async (username: string, password: string): Promise<any> => {
  return signIn(username, password);
};

export const doSignOut = async (): Promise<void> => {
  await signOut();
};

export const checkAuthentication = async (): Promise<any> => {
  return getCurrentUser();
};
