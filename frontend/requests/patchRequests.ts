import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true,
});

export const decreasePrice = async (auctionID: string, decreaseAmount: number): Promise<any> => {
  try {
    await api.patch(`/auction/dutch/decreasePrice?itemID=${auctionID}&decreaseBy=${decreaseAmount}`);
  } catch (error: unknown) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to decrease price.');
    }
    throw new Error('Failed to decrease price.');
  }
};
